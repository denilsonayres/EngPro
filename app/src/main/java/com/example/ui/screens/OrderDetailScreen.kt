package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetItem
import com.example.data.model.CheckStatus
import com.example.data.model.CompanyProfile
import com.example.data.model.ItemType
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceOrder
import com.example.ui.components.PhotoThumbnailCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TimelineStepIndicator
import com.example.ui.theme.IndustrialAmber
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.PdfReportGenerator
import com.example.util.ShareUtil
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: ServiceOrder,
    company: CompanyProfile?,
    onNavigateBack: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onAddBudgetItem: (BudgetItem) -> Unit,
    onRemoveBudgetItem: (String) -> Unit,
    onAddPhoto: (isExitPhoto: Boolean, uri: String) -> Unit,
    onUpdateOrder: (ServiceOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddBudgetItemDialog by remember { mutableStateOf(false) }
    var budgetItemType by remember { mutableStateOf(ItemType.PECA) }

    var technicalVerdictText by remember { mutableStateOf(order.initialTechnicalVerdict) }
    var isEditingVerdict by remember { mutableStateOf(false) }

    // Photo pickers
    val entryPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        uris.forEach { onAddPhoto(false, it.toString()) }
    }

    val exitPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        uris.forEach { onAddPhoto(true, it.toString()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = order.orderCode,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = order.equipmentType,
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Slate800
                        )
                    }
                },
                actions = {
                    StatusBadge(status = order.currentStatus, compact = true, modifier = Modifier.padding(end = 8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Persistent Bottom Bar for quick PDF and WhatsApp Actions
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val pdfFile = PdfReportGenerator.generateServiceOrderPdf(context, order, company)
                            ShareUtil.shareOrderPdf(context, pdfFile, order)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = IndustrialBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exportar PDF", fontWeight = FontWeight.Bold, color = IndustrialBlue)
                    }

                    Button(
                        onClick = {
                            val pdfFile = PdfReportGenerator.generateServiceOrderPdf(context, order, company)
                            ShareUtil.shareViaWhatsApp(context, order, pdfFile)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enviar WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Workflow Progress Bar & Status Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Fluxo de Trabalho em Tempo Real:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TimelineStepIndicator(currentStatus = order.currentStatus)

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Slate200, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Avançar / Alterar Status:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OrderStatus.entries.forEach { status ->
                            val isCurrent = order.status == status.code
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) status.getColor() else Slate200.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { onUpdateStatus(status) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        text = status.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) Color.White else Slate700
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab Bar
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Laudo & Checklist", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        val totalPhotos = order.entryPhotos.size + order.exitPhotos.size
                        Text("Fotos ($totalPhotos)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Orçamento", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    // TAB 0: LAUDO & CHECKLIST
                    0 -> {
                        // Equipment Summary Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    SectionHeader(title = "Equipamento & Cliente")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${order.equipmentBrand} ${order.equipmentModel}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndustrialBlue
                                    )
                                    if (order.operatingSpecs.isNotBlank()) {
                                        Text(
                                            text = order.operatingSpecs,
                                            fontSize = 12.sp,
                                            color = Slate600
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Cliente: ${order.clientName} (${order.clientPhone})",
                                        fontSize = 12.sp,
                                        color = Slate700
                                    )
                                    if (order.reportedFailure.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Queixa Inicial: ${order.reportedFailure}",
                                            fontSize = 12.sp,
                                            color = Slate800,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Technical Report / Laudo
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Laudo Técnico / Perícia Mecânica",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Slate900
                                        )
                                        IconButton(onClick = {
                                            if (isEditingVerdict) {
                                                onUpdateOrder(order.copy(initialTechnicalVerdict = technicalVerdictText))
                                                Toast.makeText(context, "Laudo salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                            }
                                            isEditingVerdict = !isEditingVerdict
                                        }) {
                                            Icon(
                                                imageVector = if (isEditingVerdict) Icons.Default.Check else Icons.Default.Edit,
                                                contentDescription = "Editar Laudo",
                                                tint = IndustrialBlue
                                            )
                                        }
                                    }

                                    if (isEditingVerdict) {
                                        OutlinedTextField(
                                            value = technicalVerdictText,
                                            onValueChange = { technicalVerdictText = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            minLines = 3,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    } else {
                                        Text(
                                            text = technicalVerdictText.ifBlank { "Nenhum laudo registrado ainda. Clique no ícone de lápis para descrever a perícia dimensional." },
                                            fontSize = 12.sp,
                                            color = Slate700,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Checklist Items
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    SectionHeader(
                                        title = "Checklist Técnico de Entrada",
                                        subtitle = "${order.entryChecklist.size} itens inspecionados no recebimento"
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    order.entryChecklist.forEachIndexed { index, item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${item.category} • ${item.name}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Slate800
                                                )
                                                if (item.note.isNotBlank()) {
                                                    Text(
                                                        text = "Obs: ${item.note}",
                                                        fontSize = 11.sp,
                                                        color = Slate600
                                                    )
                                                }
                                            }
                                            val badgeColor = when (item.status) {
                                                CheckStatus.OK -> Color(0xFF16A34A)
                                                CheckStatus.ATENCAO -> Color(0xFFD97706)
                                                CheckStatus.DEFEITO -> Color(0xFFDC2626)
                                                CheckStatus.NAO_APLICA -> Color(0xFF64748B)
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = badgeColor.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = item.status.name,
                                                    color = badgeColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        if (index < order.entryChecklist.size - 1) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Slate200)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 1: RELATÓRIO FOTOGRÁFICO
                    1 -> {
                        // Entrada / Antes
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Fotos de Entrada (Antes)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Slate900
                                            )
                                            Text(
                                                text = "Registro do estado de chegada e defeitos",
                                                fontSize = 11.sp,
                                                color = Slate500
                                            )
                                        }
                                        IconButton(onClick = {
                                            entryPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Adicionar Foto de Entrada", tint = IndustrialBlue)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (order.entryPhotos.isEmpty()) {
                                        Button(
                                            onClick = { onAddPhoto(false, "foto_entrada_simulada_${System.currentTimeMillis()}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Slate200),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Slate700)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Adicionar Foto de Chegada", color = Slate800, fontSize = 12.sp)
                                        }
                                    } else {
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(order.entryPhotos) { uri ->
                                                PhotoThumbnailCard(uri = uri, label = "Chegada")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Saída / Depois
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Fotos de Conclusão (Depois)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Slate900
                                            )
                                            Text(
                                                text = "Equipamento revisado, montado e testado",
                                                fontSize = 11.sp,
                                                color = Slate500
                                            )
                                        }
                                        IconButton(onClick = {
                                            exitPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Adicionar Foto de Saída", tint = Color(0xFF16A34A))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (order.exitPhotos.isEmpty()) {
                                        Button(
                                            onClick = { onAddPhoto(true, "foto_saida_concluida_${System.currentTimeMillis()}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Slate200),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Slate700)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Adicionar Foto Pós-Manutenção (Depois)", color = Slate800, fontSize = 12.sp)
                                        }
                                    } else {
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(order.exitPhotos) { uri ->
                                                PhotoThumbnailCard(uri = uri, label = "Concluído")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 2: ORÇAMENTO & CUSTOS
                    2 -> {
                        // Financial Summary Banner
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subtotal Peças:", fontSize = 12.sp, color = Slate600)
                                        Text(currencyFormat.format(order.partsTotal), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subtotal Serviços de Engenharia:", fontSize = 12.sp, color = Slate600)
                                        Text(currencyFormat.format(order.servicesTotal), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (order.discountPercentage > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Desconto (${order.discountPercentage.toInt()}%):", fontSize = 12.sp, color = Color(0xFFDC2626))
                                            Text("- ${currencyFormat.format(order.discountAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Slate200)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("TOTAL DO ORÇAMENTO:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                        Text(
                                            text = currencyFormat.format(order.netTotal),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = IndustrialBlue
                                        )
                                    }
                                }
                            }
                        }

                        // Peças
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Peças & Componentes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        TextButton(onClick = {
                                            budgetItemType = ItemType.PECA
                                            showAddBudgetItemDialog = true
                                        }) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Adicionar Peça", fontSize = 12.sp)
                                        }
                                    }

                                    if (order.parts.isEmpty()) {
                                        Text("Nenhuma peça adicionada ainda.", fontSize = 12.sp, color = Slate500)
                                    } else {
                                        order.parts.forEach { item ->
                                            BudgetItemRow(
                                                item = item,
                                                currencyFormat = currencyFormat,
                                                onDelete = { onRemoveBudgetItem(item.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Serviços
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Mão de Obra & Usinagem", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        TextButton(onClick = {
                                            budgetItemType = ItemType.SERVICO
                                            showAddBudgetItemDialog = true
                                        }) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Adicionar Serviço", fontSize = 12.sp)
                                        }
                                    }

                                    if (order.services.isEmpty()) {
                                        Text("Nenhum serviço adicionado ainda.", fontSize = 12.sp, color = Slate500)
                                    } else {
                                        order.services.forEach { item ->
                                            BudgetItemRow(
                                                item = item,
                                                currencyFormat = currencyFormat,
                                                onDelete = { onRemoveBudgetItem(item.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Add Part or Service
    if (showAddBudgetItemDialog) {
        AddBudgetItemDialog(
            type = budgetItemType,
            onDismiss = { showAddBudgetItemDialog = false },
            onConfirm = { newItem ->
                onAddBudgetItem(newItem)
                showAddBudgetItemDialog = false
            }
        )
    }
}

@Composable
fun BudgetItemRow(
    item: BudgetItem,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate800)
            Text(
                text = "${String.format(Locale.US, "%.1f", item.quantity)} un. x ${currencyFormat.format(item.unitPrice)}",
                fontSize = 11.sp,
                color = Slate500
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currencyFormat.format(item.subtotal),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir item",
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddBudgetItemDialog(
    type: ItemType,
    onDismiss: () -> Unit,
    onConfirm: (BudgetItem) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1.0") }
    var unitPriceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == ItemType.PECA) "Adicionar Peça / Componente" else "Adicionar Serviço Mecânico",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do item") },
                    placeholder = {
                        Text(if (type == ItemType.PECA) "Ex: Rolamento SKF 6208" else "Ex: Usinagem e Ajuste de Eixo")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Qtd / Horas") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unitPriceText,
                        onValueChange = { unitPriceText = it },
                        label = { Text("Valor Unit. (R$)") },
                        placeholder = { Text("0,00") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 1.0
                    val price = unitPriceText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank()) {
                        val item = BudgetItem(
                            id = "item_${System.currentTimeMillis()}",
                            type = type,
                            description = description.trim(),
                            quantity = qty,
                            unitPrice = price
                        )
                        onConfirm(item)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialBlue)
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
