package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyProfile
import com.example.data.model.ServiceOrder
import com.example.ui.components.PhotoThumbnailCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TimelineStepIndicator
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerPortalScreen(
    searchedOrder: ServiceOrder?,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSelectQuickOrder: (ServiceOrder) -> Unit,
    allOrders: List<ServiceOrder>,
    company: CompanyProfile?,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card: Portal do Cliente
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
                                listOf(
                                    Color(0xFF0F766E),
                                    Color(0xFF0D9488)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
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
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Portal de Acompanhamento",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Transparência total e relatório fotográfico em tempo real",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Consulte sua Ordem de Serviço",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Informe o código presente no seu comprovante ou etiqueta de recepção.",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onQueryChange,
                            placeholder = { Text("Ex: OS-2024-101") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndustrialTeal,
                                unfocusedBorderColor = Slate200
                            )
                        )

                        Button(
                            onClick = onSearchClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialTeal),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Consultar", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (allOrders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Acesso Rápido para Demonstração:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            allOrders.take(3).forEach { order ->
                                SuggestionChip(
                                    onClick = { onSelectQuickOrder(order) },
                                    label = { Text("${order.orderCode} (${order.equipmentType.take(15)}...)", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Display Searched Order Details
        if (searchedOrder != null) {
            // Live Status Timeline
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = searchedOrder.orderCode,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndustrialTeal
                                )
                                Text(
                                    text = "Data de Entrada: ${dateFormat.format(Date(searchedOrder.createdAt))}",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                            StatusBadge(status = searchedOrder.currentStatus)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        TimelineStepIndicator(currentStatus = searchedOrder.currentStatus)

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = searchedOrder.equipmentType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${searchedOrder.equipmentBrand} ${searchedOrder.equipmentModel}".trim(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IndustrialBlue
                        )
                        if (searchedOrder.serialNumber.isNotBlank()) {
                            Text(
                                text = "Nº de Série: ${searchedOrder.serialNumber}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                }
            }

            // Photographic Inspection (Before & After)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Relatório Fotográfico de Transparência",
                            subtitle = "Registro visual antes e depois da intervenção mecânica"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Fotos de Chegada
                        Text(
                            text = "1. Estado de Chegada (Antes):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (searchedOrder.entryPhotos.isEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate100, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Checklist visual realizado na bancada mecânica de recepção.", fontSize = 11.sp, color = Slate600)
                            }
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchedOrder.entryPhotos) { uri ->
                                    PhotoThumbnailCard(uri = uri, label = "Chegada")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fotos de Saída
                        Text(
                            text = "2. Conclusão & Montagem (Depois):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (searchedOrder.exitPhotos.isEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fotos pós-manutenção serão anexadas assim que o equipamento concluir a fase de montagem.", fontSize = 11.sp, color = Color(0xFF92400E))
                            }
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchedOrder.exitPhotos) { uri ->
                                    PhotoThumbnailCard(uri = uri, label = "Concluído")
                                }
                            }
                        }
                    }
                }
            }

            // Technical Verdict & Budget Approved
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(
                            title = "Laudo Técnico & Investimento",
                            subtitle = "Valores e prazos acordados"
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (searchedOrder.initialTechnicalVerdict.isNotBlank()) {
                            Text(
                                text = "Conclusão Pericial:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate600
                            )
                            Text(
                                text = searchedOrder.initialTechnicalVerdict,
                                fontSize = 12.sp,
                                color = Slate800,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Prazo de Execução:", fontSize = 11.sp, color = Slate500)
                                Text("${searchedOrder.estimatedLeadTimeDays} dias úteis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Valor Total:", fontSize = 11.sp, color = Slate500)
                                Text(
                                    text = if (searchedOrder.netTotal > 0) currencyFormat.format(searchedOrder.netTotal) else "Em Perícia",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndustrialTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Garantia: ${searchedOrder.warrantyTerms}",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
            }

            // Direct Communication Actions for the Customer
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val phone = company?.phone ?: "(19) 99345-6789"
                            ShareUtil.openContactPhone(context, phone)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Falar com a Oficina", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val pdfFile = PdfReportGenerator.generateServiceOrderPdf(context, searchedOrder, company)
                            ShareUtil.shareOrderPdf(context, pdfFile, searchedOrder)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = IndustrialBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Baixar PDF", fontWeight = FontWeight.Bold, color = IndustrialBlue, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
