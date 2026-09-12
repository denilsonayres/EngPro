package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class OrderStatus(
    val code: String,
    val title: String,
    val stepIndex: Int,
    val hexColor: Long
) {
    EM_ANALISE("EM_ANALISE", "Em Análise / Perícia", 0, 0xFF0284C7),
    ORCAMENTO_ENVIADO("ORCAMENTO_ENVIADO", "Orçamento Enviado", 1, 0xFFD97706),
    AGUARDANDO_PECA("AGUARDANDO_PECA", "Aguardando Peças", 2, 0xFFE11D48),
    EM_MANUTENCAO("EM_MANUTENCAO", "Em Manutenção", 3, 0xFF4F46E5),
    EM_TESTE("EM_TESTE", "Em Testes / Calibração", 4, 0xFF0D9488),
    CONCLUIDO("CONCLUIDO", "Concluído", 5, 0xFF16A34A),
    ENTREGUE("ENTREGUE", "Entregue ao Cliente", 6, 0xFF475569);

    fun getColor(): Color = Color(hexColor)

    companion object {
        fun fromCode(code: String): OrderStatus {
            return entries.find { it.code == code } ?: EM_ANALISE
        }
    }
}
