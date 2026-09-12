package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_orders")
data class ServiceOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderCode: String,
    val clientName: String,
    val clientCnpjCpf: String = "",
    val clientContactPerson: String = "",
    val clientPhone: String,
    val clientEmail: String = "",
    val equipmentType: String,
    val equipmentBrand: String = "",
    val equipmentModel: String = "",
    val serialNumber: String = "",
    val operatingSpecs: String = "",
    val reportedFailure: String = "",
    val initialTechnicalVerdict: String = "",
    val status: String = OrderStatus.EM_ANALISE.code,
    val entryChecklist: List<ChecklistItem> = emptyList(),
    val entryPhotos: List<String> = emptyList(),
    val exitPhotos: List<String> = emptyList(),
    val parts: List<BudgetItem> = emptyList(),
    val services: List<BudgetItem> = emptyList(),
    val discountPercentage: Double = 0.0,
    val paymentTerms: String = "28 dias no boleto faturado ou 50% entrada e 50% na retirada",
    val warrantyTerms: String = "Garantia mecânica de 90 dias conforme norma ABNT/NBR para peças e serviços executados.",
    val estimatedLeadTimeDays: Int = 5,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val currentStatus: OrderStatus
        get() = OrderStatus.fromCode(status)

    val partsTotal: Double
        get() = parts.sumOf { it.subtotal }

    val servicesTotal: Double
        get() = services.sumOf { it.subtotal }

    val grossTotal: Double
        get() = partsTotal + servicesTotal

    val discountAmount: Double
        get() = grossTotal * (discountPercentage.coerceIn(0.0, 100.0) / 100.0)

    val netTotal: Double
        get() = (grossTotal - discountAmount).coerceAtLeast(0.0)
}
