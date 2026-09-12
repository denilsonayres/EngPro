package com.example.data.model

enum class ItemType {
    PECA,
    SERVICO
}

data class BudgetItem(
    val id: String,
    val type: ItemType,
    val description: String,
    val quantity: Double,
    val unitPrice: Double
) {
    val subtotal: Double
        get() = quantity * unitPrice
}
