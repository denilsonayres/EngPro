package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import com.example.data.model.BudgetItem
import com.example.data.model.CheckStatus
import com.example.data.model.CompanyProfile
import com.example.data.model.ItemType
import com.example.data.model.ServiceOrder
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun generateServiceOrderPdf(
        context: Context,
        order: ServiceOrder,
        company: CompanyProfile?
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawPdfContent(canvas, order, company)

        pdfDocument.finishPage(page)

        val outputFile = File(context.cacheDir, "Relatorio_${order.orderCode}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun drawPdfContent(
        canvas: Canvas,
        order: ServiceOrder,
        company: CompanyProfile?
    ) {
        val paint = Paint()
        val textPaint = Paint().apply { isAntiAlias = true }
        var y = 28f
        val left = 36f
        val right = 559f
        val contentWidth = right - left

        // Header Background Banner
        paint.color = Color.parseColor("#0F52BA")
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, 595f, 75f, paint)

        // Accent strip
        paint.color = Color.parseColor("#D97706")
        canvas.drawRect(0f, 75f, 595f, 79f, paint)

        // Company Name in Header
        textPaint.color = Color.WHITE
        textPaint.textSize = 15f
        textPaint.isFakeBoldText = true
        val compName = company?.companyName ?: "MECÂNICA INDUSTRIAL PRO"
        canvas.drawText(compName, left, 35f, textPaint)

        // Company Sub-details
        textPaint.textSize = 8.5f
        textPaint.isFakeBoldText = false
        val compCnpjPhone = "CNPJ: ${company?.cnpj ?: "N/A"} | Contato: ${company?.phone ?: "N/A"} | ${company?.email ?: ""}"
        canvas.drawText(compCnpjPhone, left, 49f, textPaint)
        val compTech = "Resp. Técnico: ${company?.technicalManager ?: "Engenharia Mecânica Especializada"}"
        canvas.drawText(compTech, left, 62f, textPaint)

        // OS Title Ribbon
        y = 96f
        textPaint.color = Color.parseColor("#0B132B")
        textPaint.textSize = 13f
        textPaint.isFakeBoldText = true
        canvas.drawText("ORDEM DE SERVIÇO & LAUDO TÉCNICO MECÂNICO", left, y, textPaint)

        // OS Code on Right
        val codeText = order.orderCode
        val codeWidth = textPaint.measureText(codeText)
        textPaint.color = Color.parseColor("#0F52BA")
        canvas.drawText(codeText, right - codeWidth, y, textPaint)

        // Date & Status Subhead
        y += 14f
        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 8.5f
        textPaint.isFakeBoldText = false
        val emittedText = "Emissão: ${dateFormat.format(Date(order.createdAt))}  |  Status Atual: ${order.currentStatus.title.uppercase()}"
        canvas.drawText(emittedText, left, y, textPaint)

        // Section 1: Dados do Cliente e Equipamento (Two Column Box)
        y += 12f
        drawBoxHeader(canvas, "1. DADOS DO CLIENTE & EQUIPAMENTO", left, y, contentWidth)
        y += 16f

        paint.color = Color.parseColor("#F8FAFC")
        paint.style = Paint.Style.FILL
        val clientBoxRect = RectF(left, y, right, y + 68f)
        canvas.drawRoundRect(clientBoxRect, 4f, 4f, paint)
        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.8f
        canvas.drawRoundRect(clientBoxRect, 4f, 4f, paint)

        // Client Info Left
        var rowY = y + 14f
        drawField(canvas, "Cliente:", order.clientName, left + 8f, rowY, 240f)
        rowY += 13f
        drawField(canvas, "Contato:", "${order.clientContactPerson} - ${order.clientPhone}", left + 8f, rowY, 240f)
        rowY += 13f
        drawField(canvas, "CNPJ/CPF:", order.clientCnpjCpf.ifBlank { "Não informado" }, left + 8f, rowY, 240f)
        rowY += 13f
        drawField(canvas, "E-mail:", order.clientEmail.ifBlank { "Não informado" }, left + 8f, rowY, 240f)

        // Equipment Info Right
        val col2 = left + 265f
        rowY = y + 14f
        drawField(canvas, "Equipamento:", order.equipmentType, col2, rowY, 250f)
        rowY += 13f
        drawField(canvas, "Marca / Modelo:", "${order.equipmentBrand} / ${order.equipmentModel}", col2, rowY, 250f)
        rowY += 13f
        drawField(canvas, "Nº Série:", order.serialNumber.ifBlank { "N/A" }, col2, rowY, 250f)
        rowY += 13f
        drawField(canvas, "Especificações:", order.operatingSpecs.ifBlank { "Padrão Industrial" }, col2, rowY, 250f)

        y += 76f

        // Section 2: Defeito e Checklist Técnico de Entrada
        drawBoxHeader(canvas, "2. SINTOMAS & CHECKLIST TÉCNICO DE ENTRADA", left, y, contentWidth)
        y += 15f

        textPaint.color = Color.parseColor("#1E293B")
        textPaint.textSize = 8.5f
        textPaint.isFakeBoldText = true
        canvas.drawText("Sintoma Reclamado: ", left, y, textPaint)
        textPaint.isFakeBoldText = false
        val failureText = order.reportedFailure.ifBlank { "Revisão geral e laudo pericial" }
        canvas.drawText(truncateText(failureText, 85), left + 90f, y, textPaint)

        y += 14f

        // Draw checklist items summary
        val itemsToDraw = order.entryChecklist.take(6)
        if (itemsToDraw.isNotEmpty()) {
            for (chk in itemsToDraw) {
                val statusColor = when (chk.status) {
                    CheckStatus.OK -> Color.parseColor("#16A34A")
                    CheckStatus.ATENCAO -> Color.parseColor("#D97706")
                    CheckStatus.DEFEITO -> Color.parseColor("#DC2626")
                    CheckStatus.NAO_APLICA -> Color.parseColor("#64748B")
                }
                paint.color = statusColor
                paint.style = Paint.Style.FILL
                canvas.drawCircle(left + 6f, y - 3f, 3.5f, paint)

                textPaint.color = Color.parseColor("#0F172A")
                textPaint.textSize = 8f
                textPaint.isFakeBoldText = true
                val chkTag = "[${chk.status.name}] ${chk.category}: "
                canvas.drawText(chkTag, left + 14f, y, textPaint)

                val tagWidth = textPaint.measureText(chkTag)
                textPaint.isFakeBoldText = false
                textPaint.color = Color.parseColor("#334155")
                val detail = if (chk.note.isNotBlank()) "${chk.name} - Obs: ${chk.note}" else chk.name
                canvas.drawText(truncateText(detail, 70), left + 14f + tagWidth, y, textPaint)
                y += 11f
            }
        }

        y += 8f

        // Section 3: Orçamento Detalhado (Peças e Serviços)
        drawBoxHeader(canvas, "3. DEMONSTRATIVO DE PEÇAS & SERVIÇOS DE ENGENHARIA", left, y, contentWidth)
        y += 16f

        // Table Header
        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.FILL
        canvas.drawRect(left, y - 10f, right, y + 4f, paint)

        textPaint.color = Color.parseColor("#1E293B")
        textPaint.textSize = 8f
        textPaint.isFakeBoldText = true
        canvas.drawText("ITEM / DESCRIÇÃO", left + 6f, y, textPaint)
        canvas.drawText("TIPO", left + 280f, y, textPaint)
        canvas.drawText("QTD", left + 345f, y, textPaint)
        canvas.drawText("VALOR UNIT.", left + 395f, y, textPaint)
        canvas.drawText("SUBTOTAL", right - 65f, y, textPaint)
        y += 12f

        val allBudget = (order.parts + order.services).take(9)
        textPaint.isFakeBoldText = false

        for (item in allBudget) {
            textPaint.color = Color.parseColor("#1E293B")
            textPaint.textSize = 8f
            canvas.drawText(truncateText(item.description, 48), left + 6f, y, textPaint)

            val typeBadge = if (item.type == ItemType.PECA) "PEÇA" else "SERVIÇO"
            canvas.drawText(typeBadge, left + 280f, y, textPaint)
            canvas.drawText(String.format(Locale.US, "%.1f", item.quantity), left + 345f, y, textPaint)
            canvas.drawText(currencyFormat.format(item.unitPrice), left + 395f, y, textPaint)

            textPaint.isFakeBoldText = true
            canvas.drawText(currencyFormat.format(item.subtotal), right - 65f, y, textPaint)
            textPaint.isFakeBoldText = false

            // Light divider
            paint.color = Color.parseColor("#F1F5F9")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 0.5f
            canvas.drawLine(left, y + 3f, right, y + 3f, paint)

            y += 12.5f
        }

        y += 6f

        // Totals Box
        paint.color = Color.parseColor("#F8FAFC")
        paint.style = Paint.Style.FILL
        val totalsBox = RectF(right - 220f, y, right, y + 54f)
        canvas.drawRoundRect(totalsBox, 4f, 4f, paint)
        paint.color = Color.parseColor("#CBD5E1")
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(totalsBox, 4f, 4f, paint)

        var totY = y + 13f
        textPaint.textSize = 8f
        textPaint.color = Color.parseColor("#475569")
        canvas.drawText("Subtotal Peças / Serviços:", right - 212f, totY, textPaint)
        canvas.drawText(currencyFormat.format(order.grossTotal), right - 75f, totY, textPaint)

        totY += 12f
        if (order.discountPercentage > 0) {
            canvas.drawText("Desconto Especial (${order.discountPercentage.toInt()}%):", right - 212f, totY, textPaint)
            canvas.drawText("- ${currencyFormat.format(order.discountAmount)}", right - 75f, totY, textPaint)
            totY += 12f
        }

        textPaint.textSize = 10f
        textPaint.color = Color.parseColor("#0F52BA")
        textPaint.isFakeBoldText = true
        canvas.drawText("VALOR TOTAL LÍQUIDO:", right - 212f, totY, textPaint)
        canvas.drawText(currencyFormat.format(order.netTotal), right - 85f, totY, textPaint)

        // Commercial Terms on the Left
        textPaint.textSize = 8f
        textPaint.color = Color.parseColor("#334155")
        textPaint.isFakeBoldText = true
        canvas.drawText("Condições Comerciais:", left + 4f, y + 12f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText("• Prazo de Execução: ${order.estimatedLeadTimeDays} dias úteis", left + 4f, y + 24f, textPaint)
        canvas.drawText("• Condições de Pagamento: ${truncateText(order.paymentTerms, 45)}", left + 4f, y + 36f, textPaint)
        canvas.drawText("• Garantia: ${truncateText(order.warrantyTerms, 50)}", left + 4f, y + 48f, textPaint)

        y += 72f

        // Section 4: Assinaturas
        paint.color = Color.parseColor("#94A3B8")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.8f

        val sigWidth = 190f
        // Tech signature line
        canvas.drawLine(left + 20f, y + 30f, left + 20f + sigWidth, y + 30f, paint)
        textPaint.textSize = 8f
        textPaint.color = Color.parseColor("#334155")
        textPaint.isFakeBoldText = true
        canvas.drawText("Responsável Técnico Mecânico", left + 35f, y + 41f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText(company?.technicalManager ?: "Engenheiro Responsável", left + 30f, y + 51f, textPaint)

        // Client approval signature line
        canvas.drawLine(right - 20f - sigWidth, y + 30f, right - 20f, y + 30f, paint)
        textPaint.isFakeBoldText = true
        canvas.drawText("De Acordo / Aprovação do Cliente", right - 190f, y + 41f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText("${order.clientName} - Data: ___/___/______", right - 200f, y + 51f, textPaint)

        // Footer
        paint.color = Color.parseColor("#0F52BA")
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 825f, 595f, 842f, paint)
        textPaint.color = Color.WHITE
        textPaint.textSize = 7.5f
        val footerNotice = "Documento gerado por MecânicaOS - Sistema SaaS para Oficinas de Engenharia Mecânica | Acompanhe pelo portal com o código ${order.orderCode}"
        canvas.drawText(footerNotice, 36f, 836f, textPaint)
    }

    private fun drawBoxHeader(canvas: Canvas, title: String, left: Float, y: Float, width: Float) {
        val paint = Paint().apply {
            color = Color.parseColor("#0F52BA")
            style = Paint.Style.FILL
        }
        canvas.drawRect(left, y - 8f, left + 4f, y + 4f, paint)

        val textPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9.5f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(title, left + 10f, y + 2f, textPaint)
    }

    private fun drawField(canvas: Canvas, label: String, value: String, x: Float, y: Float, maxW: Float) {
        val labelPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 8f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 8f
            isFakeBoldText = false
            isAntiAlias = true
        }
        canvas.drawText(label, x, y, labelPaint)
        val lWidth = labelPaint.measureText(label)
        canvas.drawText(truncateText(value, 35), x + lWidth + 4f, y, valuePaint)
    }

    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) text.take(maxLength - 3) + "..." else text
    }
}
