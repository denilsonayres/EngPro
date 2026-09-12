package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ServiceOrder
import java.io.File
import java.net.URLEncoder

object ShareUtil {

    fun shareOrderPdf(context: Context, pdfFile: File, order: ServiceOrder) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val fileUri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val shareMessage = buildClientMessage(order)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "Relatório e Orçamento ${order.orderCode} - ${order.equipmentType}")
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Enviar Orçamento / Relatório via:")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao compartilhar PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun shareViaWhatsApp(context: Context, order: ServiceOrder, pdfFile: File? = null) {
        try {
            val rawPhone = order.clientPhone.filter { it.isDigit() }
            val cleanPhone = if (rawPhone.startsWith("55")) rawPhone else "55$rawPhone"
            val message = buildClientMessage(order)
            val encodedMessage = URLEncoder.encode(message, "UTF-8")

            val waUri = if (cleanPhone.length >= 10) {
                Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage")
            } else {
                Uri.parse("https://api.whatsapp.com/send?text=$encodedMessage")
            }

            val intent = Intent(Intent.ACTION_VIEW, waUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

            // If PDF is also available, suggest sharing attachment
            if (pdfFile != null && pdfFile.exists()) {
                Toast.makeText(context, "Mensagem enviada! Você também pode anexar o PDF pelo menu de compartilhamento.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // Fallback to generic share
            shareText(context, buildClientMessage(order), "Compartilhar OS ${order.orderCode}")
        }
    }

    fun shareText(context: Context, text: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível compartilhar", Toast.LENGTH_SHORT).show()
        }
    }

    fun openContactPhone(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phone.filter { it.isDigit() || it == '+' }}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    private fun buildClientMessage(order: ServiceOrder): String {
        return """
            🛠️ *ORDEM DE SERVIÇO MECÂNICA - ${order.orderCode}*
            
            Olá, *${order.clientName}*!
            
            Informamos a atualização do seu equipamento mecânico:
            ⚙️ *Equipamento:* ${order.equipmentType} (${order.equipmentBrand} ${order.equipmentModel})
            📍 *Status Atual:* *${order.currentStatus.title.uppercase()}*
            
            💰 *Total do Orçamento:* R$ ${String.format(java.util.Locale.US, "%.2f", order.netTotal)}
            ⏱️ *Prazo de Execução:* ${order.estimatedLeadTimeDays} dias úteis
            
            🔍 *Acompanhe em Tempo Real:*
            Utilize o código exclusivo *${order.orderCode}* no Portal do Cliente MecânicaOS para ver o laudo e fotos de inspeção antes e depois do conserto.
            
            Atenciosamente,
            Equipe de Engenharia Mecânica & Manutenção Industrial
        """.trimIndent()
    }
}
