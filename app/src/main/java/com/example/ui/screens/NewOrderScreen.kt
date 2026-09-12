package com.example.ui.screens

import android.net.Uri
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CheckStatus
import com.example.data.model.ChecklistItem
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceOrder
import com.example.ui.components.PhotoThumbnailCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewOrderScreen(
    onNavigateBack: () -> Unit,
    onOrderCreated: (ServiceOrder) -> Unit,
    existingOrdersCount: Int,
    modifier: Modifier = Modifier
) {
    // Generate sequential OS Code
    val generatedCode = remember {
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val seq = 101 + existingOrdersCount
        "OS-$year-$seq"
    }

    // Client Fields
    var clientName by remember { mutableStateOf("") }
    var clientCnpj by remember { mutableStateOf("") }
    var clientContact by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }

    // Equipment Fields
    var equipmentType by remember { mutableStateOf("") }
    var equipmentBrand by remember { mutableStateOf("") }
    var equipmentModel by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var operatingSpecs by remember { mutableStateOf("") }
    var reportedFailure by remember { mutableStateOf("") }

    // Estimated Days
    var estimatedDays by remember { mutableIntStateOf(5) }

    // Technical Checklist Items
    val checklistItems = remember {
        mutableStateListOf<ChecklistItem>().apply {
            addAll(ChecklistItem.getDefaultMechanicalChecklist())
        }
    }

    // Photos
    val entryPhotos = remember { mutableStateListOf<String>() }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 6)
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            entryPhotos.add(uri.toString())
        }
    }

    val quickEquipments = listOf(
        "Redutor de Velocidade",
        "Bomba Hidráulica de Pistões",
        "Compressor Parafuso",
        "Motor Elétrico Trifásico",
        "Cilindro Hidráulico",
        "Eixo Cardan / Usinagem",
        "Mancal / Turbina"
    )

    var hasError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Recepção & Checklist de Entrada",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = generatedCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndustrialBlue
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // STEP 1: CLIENT DATA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "1. Dados do Cliente / Empresa",
                            subtitle = "Identificação para contato e faturamento"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = clientName,
                            onValueChange = { clientName = it; hasError = false },
                            label = { Text("Razão Social / Nome do Cliente *") },
                            placeholder = { Text("Ex: Usina Açucareira Vale Verde Ltda") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            isError = hasError && clientName.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = clientContact,
                                onValueChange = { clientContact = it },
                                label = { Text("Contato / Técnico") },
                                placeholder = { Text("Ex: Carlos Manutenção") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = clientPhone,
                                onValueChange = { clientPhone = it },
                                label = { Text("WhatsApp / Fone *") },
                                placeholder = { Text("(19) 99123-4567") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = clientCnpj,
                                onValueChange = { clientCnpj = it },
                                label = { Text("CNPJ / CPF") },
                                placeholder = { Text("00.000.000/0001-00") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = clientEmail,
                                onValueChange = { clientEmail = it },
                                label = { Text("E-mail para envio") },
                                placeholder = { Text("compras@empresa.com") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // STEP 2: MECHANICAL EQUIPMENT
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "2. Dados do Equipamento Mecânico",
                            subtitle = "Especificações industriais (não automotivo)"
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Select chips
                        Text(
                            text = "Sugestões de Equipamentos Comuns:",
                            fontSize = 11.sp,
                            color = Slate500,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            quickEquipments.forEach { item ->
                                SuggestionChip(
                                    onClick = { equipmentType = item; hasError = false },
                                    label = { Text(item, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = equipmentType,
                            onValueChange = { equipmentType = it; hasError = false },
                            label = { Text("Tipo de Equipamento Mecânico *") },
                            placeholder = { Text("Ex: Redutor de Eixos Paralelos") },
                            leadingIcon = { Icon(Icons.Default.Engineering, contentDescription = null) },
                            isError = hasError && equipmentType.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = equipmentBrand,
                                onValueChange = { equipmentBrand = it },
                                label = { Text("Marca / Fabricante") },
                                placeholder = { Text("Ex: SEW, Flender, Rexroth") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = equipmentModel,
                                onValueChange = { equipmentModel = it },
                                label = { Text("Modelo / Linha") },
                                placeholder = { Text("Ex: MC3PL07 - 1:45") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = serialNumber,
                                onValueChange = { serialNumber = it },
                                label = { Text("Nº de Série / Tag") },
                                placeholder = { Text("SN-849201") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = operatingSpecs,
                                onValueChange = { operatingSpecs = it },
                                label = { Text("Especificações (RPM / Potência)") },
                                placeholder = { Text("Ex: 1750 RPM | 75 kW") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = reportedFailure,
                            onValueChange = { reportedFailure = it },
                            label = { Text("Sintoma Reclamado / Queixa do Cliente") },
                            placeholder = { Text("Ex: Vibração anormal, aquecimento elevado e vazamento no retentor dianteiro.") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // STEP 3: TECHNICAL CHECKLIST (SHOP FLOOR USABILITY)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "3. Checklist Técnico de Entrada",
                            subtitle = "Perícia rápida no recebimento do equipamento"
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        checklistItems.forEachIndexed { index, item ->
                            ChecklistItemRow(
                                item = item,
                                onStatusChange = { newStatus ->
                                    checklistItems[index] = item.copy(status = newStatus)
                                },
                                onNoteChange = { newNote ->
                                    checklistItems[index] = item.copy(note = newNote)
                                }
                            )
                            if (index < checklistItems.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate200)
                            }
                        }
                    }
                }
            }

            // STEP 4: PHOTOGRAPHIC REPORT (ANTES / CHEGADA)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "4. Relatório Fotográfico de Chegada",
                            subtitle = "Fotos do estado de recebimento para transparência com o cliente"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Galeria")
                            }

                            Button(
                                onClick = {
                                    // Add sample placeholder entry photo if no physical camera photo picked
                                    entryPhotos.add("sample_entry_photo_${System.currentTimeMillis()}")
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Foto Rápida")
                            }
                        }

                        if (entryPhotos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(entryPhotos) { uri ->
                                    PhotoThumbnailCard(
                                        uri = uri,
                                        label = "Chegada",
                                        onRemove = { entryPhotos.remove(uri) }
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dica: Anexe fotos de trincas, vazamentos e placa de identificação do equipamento.",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            }

            // Error notice
            if (hasError) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEE2E2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                    ) {
                        Text(
                            text = "Atenção: Preencha o Nome do Cliente e o Tipo de Equipamento para prosseguir.",
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // SUBMIT BUTTON
            item {
                Button(
                    onClick = {
                        if (clientName.isBlank() || equipmentType.isBlank()) {
                            hasError = true
                            return@Button
                        }

                        val newOrder = ServiceOrder(
                            orderCode = generatedCode,
                            clientName = clientName.trim(),
                            clientCnpjCpf = clientCnpj.trim(),
                            clientContactPerson = clientContact.trim(),
                            clientPhone = clientPhone.ifBlank { "(19) 99999-0000" }.trim(),
                            clientEmail = clientEmail.trim(),
                            equipmentType = equipmentType.trim(),
                            equipmentBrand = equipmentBrand.trim(),
                            equipmentModel = equipmentModel.trim(),
                            serialNumber = serialNumber.trim(),
                            operatingSpecs = operatingSpecs.trim(),
                            reportedFailure = reportedFailure.trim(),
                            initialTechnicalVerdict = "Equipamento recepcionado na bancada. Checklist de entrada registrado com ${checklistItems.count { it.status == CheckStatus.DEFEITO }} não-conformidade(s).",
                            status = OrderStatus.EM_ANALISE.code,
                            entryChecklist = checklistItems.toList(),
                            entryPhotos = entryPhotos.toList(),
                            estimatedLeadTimeDays = estimatedDays
                        )

                        onOrderCreated(newOrder)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialBlue)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cadastrar Equipamento & Emitir OS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onStatusChange: (CheckStatus) -> Unit,
    onNoteChange: (String) -> Unit
) {
    var expandedNote by remember { mutableStateOf(item.note.isNotBlank()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Slate500
                )
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate800
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Status Choice Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CheckStatusButton(
                label = "OK",
                isSelected = item.status == CheckStatus.OK,
                selectedColor = Color(0xFF16A34A),
                onClick = { onStatusChange(CheckStatus.OK) },
                modifier = Modifier.weight(1f)
            )
            CheckStatusButton(
                label = "Atenção",
                isSelected = item.status == CheckStatus.ATENCAO,
                selectedColor = Color(0xFFD97706),
                onClick = { onStatusChange(CheckStatus.ATENCAO) },
                modifier = Modifier.weight(1.1f)
            )
            CheckStatusButton(
                label = "Defeito",
                isSelected = item.status == CheckStatus.DEFEITO,
                selectedColor = Color(0xFFDC2626),
                onClick = { onStatusChange(CheckStatus.DEFEITO) },
                modifier = Modifier.weight(1.1f)
            )
            CheckStatusButton(
                label = "N/A",
                isSelected = item.status == CheckStatus.NAO_APLICA,
                selectedColor = Color(0xFF64748B),
                onClick = { onStatusChange(CheckStatus.NAO_APLICA) },
                modifier = Modifier.weight(0.9f)
            )
        }

        // Observation note toggle
        if (item.status == CheckStatus.DEFEITO || item.status == CheckStatus.ATENCAO || expandedNote) {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = item.note,
                onValueChange = onNoteChange,
                placeholder = { Text("Detalhe a folga, desgaste ou trinca observada...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun CheckStatusButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) selectedColor else Slate200.copy(alpha = 0.6f)
    val contentColor = if (isSelected) Color.White else Slate700

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
