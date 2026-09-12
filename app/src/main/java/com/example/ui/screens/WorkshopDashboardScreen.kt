package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyProfile
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceOrder
import com.example.ui.components.KpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.IndustrialAmber
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.IndustrialTeal
import com.example.ui.theme.Slate100
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
fun WorkshopDashboardScreen(
    orders: List<ServiceOrder>,
    allOrdersCount: Int,
    company: CompanyProfile?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: OrderStatus?,
    onFilterSelect: (OrderStatus?) -> Unit,
    onOrderClick: (Long) -> Unit,
    onNewOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    val countAnalysis = orders.count { it.status == OrderStatus.EM_ANALISE.code }
    val countMaintenance = orders.count { it.status == OrderStatus.EM_MANUTENCAO.code || it.status == OrderStatus.AGUARDANDO_PECA.code }
    val countDone = orders.count { it.status == OrderStatus.CONCLUIDO.code || it.status == OrderStatus.ENTREGUE.code }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // SaaS Workshop Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        IndustrialBlue,
                                        Color(0xFF1E3A8A)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Engineering,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = company?.tradingName ?: "MecânicaOS Pro",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp
                                        )
                                        Text(
                                            text = company?.technicalManager ?: "Engenharia Mecânica & Usinagem",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IndustrialAmber.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, IndustrialAmber)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "SaaS Ativo",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gestão Técnica de Ordens de Serviço & Portal do Cliente",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // KPI Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Em Análise",
                        count = countAnalysis.toString(),
                        subtitle = "Perícia inicial",
                        icon = Icons.Default.HourglassTop,
                        accentColor = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterSelect(OrderStatus.EM_ANALISE) }
                    )
                    KpiCard(
                        title = "Em Execução",
                        count = countMaintenance.toString(),
                        subtitle = "Usinagem/Peças",
                        icon = Icons.Default.Build,
                        accentColor = IndustrialAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterSelect(OrderStatus.EM_MANUTENCAO) }
                    )
                    KpiCard(
                        title = "Concluídos",
                        count = countDone.toString(),
                        subtitle = "Prontos/Entregues",
                        icon = Icons.Default.CheckCircle,
                        accentColor = IndustrialTeal,
                        modifier = Modifier.weight(1f),
                        onClick = { onFilterSelect(OrderStatus.CONCLUIDO) }
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por código (ex: OS-101), equipamento ou cliente...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Slate400
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Slate200
                    ),
                    singleLine = true
                )
            }

            // Status Filter Horizontal Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onFilterSelect(null) },
                        label = { Text("Todas ($allOrdersCount)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndustrialBlue,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    OrderStatus.entries.forEach { status ->
                        val count = orders.count { it.status == status.code }
                        FilterChip(
                            selected = selectedFilter == status,
                            onClick = { onFilterSelect(if (selectedFilter == status) null else status) },
                            label = { Text("${status.title.split("/").first().trim()} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = status.getColor(),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Order List Items
            if (orders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhuma Ordem de Serviço encontrada",
                                fontWeight = FontWeight.Bold,
                                color = Slate700,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tente alterar os filtros ou cadastre um novo equipamento mecânico.",
                                color = Slate500,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    ServiceOrderCard(
                        order = order,
                        currencyFormat = currencyFormat,
                        onClick = { onOrderClick(order.id) },
                        onShareWhatsApp = {
                            ShareUtil.shareViaWhatsApp(context, order)
                        },
                        onGeneratePdf = {
                            val pdfFile = PdfReportGenerator.generateServiceOrderPdf(context, order, company)
                            ShareUtil.shareOrderPdf(context, pdfFile, order)
                        }
                    )
                }
            }
        }

        // Extended Floating Action Button: Nova Entrada / Checklist
        ExtendedFloatingActionButton(
            onClick = onNewOrderClick,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Nova Recepção", fontWeight = FontWeight.Bold) },
            containerColor = IndustrialBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        )
    }
}

@Composable
fun ServiceOrderCard(
    order: ServiceOrder,
    currencyFormat: NumberFormat,
    onClick: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onGeneratePdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Code + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = order.orderCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (order.serialNumber.isNotBlank()) {
                        Text(
                            text = "SN: ${order.serialNumber}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
                StatusBadge(status = order.currentStatus, compact = true)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Equipment Name (Headline)
            Text(
                text = order.equipmentType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Brand and Model
            if (order.equipmentBrand.isNotBlank() || order.equipmentModel.isNotBlank()) {
                Text(
                    text = "${order.equipmentBrand} ${order.equipmentModel}".trim(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IndustrialBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Client and Contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cliente: ",
                    fontSize = 12.sp,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = order.clientName,
                    fontSize = 12.sp,
                    color = Slate800,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (order.reportedFailure.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sintoma: ${order.reportedFailure}",
                    fontSize = 11.sp,
                    color = Slate600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate200, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: Value, Lead time and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Orçamento:",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                    Text(
                        text = if (order.netTotal > 0) currencyFormat.format(order.netTotal) else "Em Perícia",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (order.netTotal > 0) Slate900 else IndustrialAmber
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onShareWhatsApp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Enviar WhatsApp",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onGeneratePdf,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Gerar PDF",
                            tint = IndustrialBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ver Ordem de Serviço",
                        tint = Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
