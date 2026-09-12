package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_profile")
data class CompanyProfile(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "TecnoMecânica Engenharia & Manutenção Industrial",
    val tradingName: String = "TecnoMecânica SaaS B2B",
    val cnpj: String = "45.123.890/0001-34",
    val phone: String = "(19) 99345-6789",
    val email: String = "oficina@tecnomecanica.com.br",
    val address: String = "Av. Industrial Mecânica, 1420 - Distrito Industrial, Piracicaba - SP",
    val technicalManager: String = "Eng. Marcelo Aguiar (CREA-SP 508219)",
    val activePlan: String = "Plano Oficina Pro (SaaS B2B)",
    val subscriptionValidUntil: String = "15/12/2026",
    val isSubscriptionActive: Boolean = true
)
