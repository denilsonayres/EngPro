package com.example.data.model

enum class CheckStatus {
    OK,
    ATENCAO,
    DEFEITO,
    NAO_APLICA
}

data class ChecklistItem(
    val id: String,
    val category: String,
    val name: String,
    val status: CheckStatus = CheckStatus.OK,
    val note: String = ""
) {
    companion object {
        fun getDefaultMechanicalChecklist(): List<ChecklistItem> {
            return listOf(
                ChecklistItem(
                    id = "c1",
                    category = "Estrutura & Carcaça",
                    name = "Inspeção visual de trincas, quebras e roscas na carcaça",
                    status = CheckStatus.OK
                ),
                ChecklistItem(
                    id = "c2",
                    category = "Eixos & Acoplamentos",
                    name = "Alinhamento e empenamento de eixos de entrada/saída",
                    status = CheckStatus.ATENCAO,
                    note = "Desvio radial leve no assento do rolamento"
                ),
                ChecklistItem(
                    id = "c3",
                    category = "Mancais & Rolamentos",
                    name = "Folga axial/radial e rugosidade de pistas de rolamento",
                    status = CheckStatus.DEFEITO,
                    note = "Pitting na pista interna do rolamento dianteiro"
                ),
                ChecklistItem(
                    id = "c4",
                    category = "Vedações & Lubrificação",
                    name = "Estanqueidade de retentores, O-rings e tampas",
                    status = CheckStatus.DEFEITO,
                    note = "Vazamento severo no retentor primário"
                ),
                ChecklistItem(
                    id = "c5",
                    category = "Engrenamento & Dentes",
                    name = "Desgaste de flancos de dentes, engrenagens e chavetas",
                    status = CheckStatus.OK
                ),
                ChecklistItem(
                    id = "c6",
                    category = "Lubrificação",
                    name = "Contaminação de lubrificante por partículas metálicas",
                    status = CheckStatus.ATENCAO,
                    note = "Óleo oxidado com presença de limalha"
                )
            )
        }
    }
}
