package com.example.data.repository

import com.example.data.local.CompanyDao
import com.example.data.local.ServiceOrderDao
import com.example.data.model.BudgetItem
import com.example.data.model.CheckStatus
import com.example.data.model.ChecklistItem
import com.example.data.model.CompanyProfile
import com.example.data.model.ItemType
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceOrder
import kotlinx.coroutines.flow.Flow

class MecanicaRepository(
    private val serviceOrderDao: ServiceOrderDao,
    private val companyDao: CompanyDao
) {
    val allOrders: Flow<List<ServiceOrder>> = serviceOrderDao.getAllOrders()
    val companyProfile: Flow<CompanyProfile?> = companyDao.getCompanyProfile()

    fun getOrderById(id: Long): Flow<ServiceOrder?> = serviceOrderDao.getOrderById(id)

    fun getOrderByCode(code: String): Flow<ServiceOrder?> = serviceOrderDao.getOrderByCode(code)

    fun searchOrders(query: String): Flow<List<ServiceOrder>> = serviceOrderDao.searchOrders(query)

    suspend fun saveOrder(order: ServiceOrder): Long = serviceOrderDao.insert(order)

    suspend fun updateOrder(order: ServiceOrder) = serviceOrderDao.update(order)

    suspend fun deleteOrder(id: Long) = serviceOrderDao.deleteById(id)

    suspend fun updateCompany(company: CompanyProfile) = companyDao.update(company)

    suspend fun prepopulateIfEmpty() {
        val companyCount = companyDao.getCompanyProfile()
        companyDao.insert(CompanyProfile())

        if (serviceOrderDao.getCount() == 0) {
            val sampleOrder1 = ServiceOrder(
                orderCode = "OS-2024-101",
                clientName = "Mineração & Cerâmica São Pedro Ltda",
                clientCnpjCpf = "18.940.231/0001-55",
                clientContactPerson = "Marcio Silveira (Supervisor de Manutenção)",
                clientPhone = "(19) 99123-4567",
                clientEmail = "manutencao@ceramicapedro.com.br",
                equipmentType = "Redutor de Velocidade de Eixos Paralelos",
                equipmentBrand = "SEW-Eurodrive",
                equipmentModel = "MC3PL07 - 1:45",
                serialNumber = "SEW-99201-BR",
                operatingSpecs = "Potência: 45 kW | Redução: 1:45 | Rotação Entrada: 1750 RPM",
                reportedFailure = "Vibração excessiva em regime contínuo, aquecimento anormal na caixa do rolamento intermediário e vazamento de lubrificante sintético ISO VG 220.",
                initialTechnicalVerdict = "Folga excessiva no conjunto de rolamentos autocompensadores de rolos (SKF 22216), desgaste por pitting no flanco dos dentes do 2º estágio de engrenamento e vedação primária rompida.",
                status = OrderStatus.EM_MANUTENCAO.code,
                entryChecklist = listOf(
                    ChecklistItem("chk_1", "Carcaça & Base", "Inspeção de trincas na carcaça fundida", CheckStatus.OK, "Sem trincas na carcaça estrutural"),
                    ChecklistItem("chk_2", "Eixos & Assentos", "Batimento radial e folga nos eixos de entrada/saída", CheckStatus.DEFEITO, "Batimento radial de 0,08mm no colo do rolamento"),
                    ChecklistItem("chk_3", "Rolamentos", "Estado das pistas e rolos dos mancais", CheckStatus.DEFEITO, "Pitting acentuado e gaiola metálica deformada"),
                    ChecklistItem("chk_4", "Vedações", "Estanqueidade dos retentores e juntas de vedação", CheckStatus.DEFEITO, "Retentor lábio duplo ressecado e vazando"),
                    ChecklistItem("chk_5", "Engrenagens", "Inspeção dos dentes de engrenagens e pinhão", CheckStatus.ATENCAO, "Desgaste moderado no 2º par redutor"),
                    ChecklistItem("chk_6", "Lubrificante", "Análise visual de contaminação do óleo", CheckStatus.DEFEITO, "Óleo oxidado com presença visível de limalha")
                ),
                entryPhotos = emptyList(),
                exitPhotos = emptyList(),
                parts = listOf(
                    BudgetItem("p1", ItemType.PECA, "Rolamento Autocompensador de Rolos SKF 22216 EK", 2.0, 890.00),
                    BudgetItem("p2", ItemType.PECA, "Bucha de Fixação Cônica H 316", 2.0, 185.00),
                    BudgetItem("p3", ItemType.PECA, "Retentor Viton Duplo 80x100x12 mm", 2.0, 145.00),
                    BudgetItem("p4", ItemType.PECA, "Óleo Sintético Industrial ISO VG 220 (Balde 20L)", 1.0, 720.00),
                    BudgetItem("p5", ItemType.PECA, "Kit Juntas e Anéis O-ring Especiais", 1.0, 160.00)
                ),
                services = listOf(
                    BudgetItem("s1", ItemType.SERVICO, "Desmontagem, Lavagem Química e Perícia Dimensional", 8.0, 120.00),
                    BudgetItem("s2", ItemType.SERVICO, "Usinagem e Recuperação de Alojamento de Mancal em Torno CNC", 6.0, 150.00),
                    BudgetItem("s3", ItemType.SERVICO, "Montagem de Precisão, Ajuste de Folga de Engrenamento e Teste em Vazio", 10.0, 130.00),
                    BudgetItem("s4", ItemType.SERVICO, "Emissão de Laudo Técnico Pericial de Engenharia Mecânica", 1.0, 450.00)
                ),
                discountPercentage = 5.0,
                paymentTerms = "28 dias faturado após aprovação técnica ou boleto bancário",
                warrantyTerms = "Garantia estendida de 120 dias para peças substituídas e serviços de usinagem.",
                estimatedLeadTimeDays = 6,
                createdAt = System.currentTimeMillis() - (86400000L * 3)
            )

            val sampleOrder2 = ServiceOrder(
                orderCode = "OS-2024-102",
                clientName = "Usina Agroindustrial Vale das Palmeiras",
                clientCnpjCpf = "04.551.982/0001-19",
                clientContactPerson = "Eng. Daniel Nogueira",
                clientPhone = "(19) 98844-3322",
                clientEmail = "daniel.manutencao@agrovalle.ind.br",
                equipmentType = "Bomba Hidráulica de Pistões Axiais",
                equipmentBrand = "Bosch Rexroth",
                equipmentModel = "A4VG90EP4D1/32R",
                serialNumber = "RX-8840192",
                operatingSpecs = "Deslocamento: 90 cm³/rev | Pressão Máx: 400 bar | Vazão: 160 L/min",
                reportedFailure = "Queda de pressão no circuito da ceifadeira mecânica ao atingir temperatura operacional de 60°C.",
                initialTechnicalVerdict = "Prato oscilante com ranhuras de desgaste severo e sapatas dos pistões riscadas por microcontaminação sólida no fluido hidráulico.",
                status = OrderStatus.ORCAMENTO_ENVIADO.code,
                entryChecklist = listOf(
                    ChecklistItem("chk_10", "Carcaça Hidráulica", "Inspeção dimensional do bloco de cilindros", CheckStatus.ATENCAO, "Leve desgaste nas camisas"),
                    ChecklistItem("chk_11", "Placa de Válvulas", "Superfície de assentamento e planicidade", CheckStatus.DEFEITO, "Riscos profundos causando by-pass de fluido"),
                    ChecklistItem("chk_12", "Grupo Rotativo", "Folga do conjunto de 9 pistões e prato oscilante", CheckStatus.DEFEITO, "Sapatas gastas acima da tolerância OEM"),
                    ChecklistItem("chk_13", "Vedações Dinâmicas", "Retentores do eixo estriado e anéis fluorocarbono", CheckStatus.DEFEITO, "Troca mandatória do kit de vedações")
                ),
                entryPhotos = emptyList(),
                exitPhotos = emptyList(),
                parts = listOf(
                    BudgetItem("p10", ItemType.PECA, "Kit Grupo Rotativo Original Rexroth (Bloco + Pistões + Prato)", 1.0, 6400.00),
                    BudgetItem("p11", ItemType.PECA, "Kit Completo de Vedações FKM Alta Pressão", 1.0, 780.00),
                    BudgetItem("p12", ItemType.PECA, "Placa de Distribuição Retificada e Nitretada", 1.0, 1250.00)
                ),
                services = listOf(
                    BudgetItem("s10", ItemType.SERVICO, "Desmontagem em bancada limpa e análise micrométrica", 5.0, 140.00),
                    BudgetItem("s11", ItemType.SERVICO, "Lapeamento de Superfícies de Alta Precisão (Ra 0,2)", 4.0, 180.00),
                    BudgetItem("s12", ItemType.SERVICO, "Montagem, Calibração e Teste Dinâmico em Bancada de Testes 350 bar", 6.0, 160.00)
                ),
                discountPercentage = 0.0,
                paymentTerms = "Sinal de 40% na autorização + saldo faturado 30 dias",
                warrantyTerms = "Garantia de 6 meses para o conjunto rotativo e estanqueidade.",
                estimatedLeadTimeDays = 4,
                createdAt = System.currentTimeMillis() - (86400000L * 1)
            )

            val sampleOrder3 = ServiceOrder(
                orderCode = "OS-2024-103",
                clientName = "Metalúrgica Forja Brasil",
                clientCnpjCpf = "61.229.401/0001-88",
                clientContactPerson = "Gilberto Ferreira",
                clientPhone = "(19) 97112-9900",
                clientEmail = "forja.gilberto@metalbrasil.com",
                equipmentType = "Compressor de Ar de Parafuso Industrial",
                equipmentBrand = "Atlas Copco",
                equipmentModel = "GA 37 VSD (50 HP)",
                serialNumber = "AC-551029",
                operatingSpecs = "Potência: 37 kW (50 HP) | Pressão: 10 bar | 24.500 horas de operação",
                reportedFailure = "Alarme de alta temperatura do elemento compressor (>105°C) e desarme térmico de emergência.",
                initialTechnicalVerdict = "Revisão geral de 24.000h: elemento compressor com folgas dentro do limite, porém válvula termostática travada fechada e trocador de calor colmatado.",
                status = OrderStatus.CONCLUIDO.code,
                entryChecklist = listOf(
                    ChecklistItem("chk_20", "Elemento Compressor", "Folga dos rotores fêmea e macho", CheckStatus.OK, "Dentro da tolerância original de fábrica"),
                    ChecklistItem("chk_21", "Válvula Termostática", "Atuação do termoelemento a 71°C", CheckStatus.DEFEITO, "Termoelemento inoperante"),
                    ChecklistItem("chk_22", "Radiador / Trocador", "Desobstrução da colmeia de resfriamento", CheckStatus.DEFEITO, "Colmeia 70% entupida por particulado"),
                    ChecklistItem("chk_23", "Filtros & Separador", "Pressão diferencial no filtro separador ar/óleo", CheckStatus.DEFEITO, "Filtro saturado com pressão diferencial alta")
                ),
                entryPhotos = emptyList(),
                exitPhotos = emptyList(),
                parts = listOf(
                    BudgetItem("p20", ItemType.PECA, "Kit Manutenção Preventiva 24k Horas Atlas Copco", 1.0, 3200.00),
                    BudgetItem("p21", ItemType.PECA, "Termoelemento 71°C para Válvula By-pass de Óleo", 1.0, 480.00),
                    BudgetItem("p22", ItemType.PECA, "Óleo Roto-Inject Fluid Atlas Copco (20 Litros)", 1.0, 1150.00)
                ),
                services = listOf(
                    BudgetItem("s20", ItemType.SERVICO, "Limpeza Química Ultrassônica de Trocador de Calor de Alumínio", 1.0, 850.00),
                    BudgetItem("s21", ItemType.SERVICO, "Substituição de Filtros, Válvula e Calibração do Inversor VSD", 6.0, 120.00),
                    BudgetItem("s22", ItemType.SERVICO, "Teste de Rendimento Volumétrico e Análise Termográfica de 2 Horas", 1.0, 400.00)
                ),
                discountPercentage = 8.0,
                paymentTerms = "Faturado 15/30 dias",
                warrantyTerms = "Garantia de 90 dias com suporte técnico presencial se necessário.",
                estimatedLeadTimeDays = 3,
                createdAt = System.currentTimeMillis() - (86400000L * 5)
            )

            serviceOrderDao.insert(sampleOrder1)
            serviceOrderDao.insert(sampleOrder2)
            serviceOrderDao.insert(sampleOrder3)
        }
    }
}
