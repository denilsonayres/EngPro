package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BudgetItem
import com.example.data.model.CheckStatus
import com.example.data.model.ChecklistItem
import com.example.data.model.ItemType
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {
        }
        return list
    }

    @TypeConverter
    fun fromChecklist(list: List<ChecklistItem>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("category", item.category)
            obj.put("name", item.name)
            obj.put("status", item.status.name)
            obj.put("note", item.note)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toChecklist(data: String?): List<ChecklistItem> {
        if (data.isNullOrBlank()) return emptyList()
        val list = mutableListOf<ChecklistItem>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val statusStr = obj.optString("status", CheckStatus.OK.name)
                val status = try {
                    CheckStatus.valueOf(statusStr)
                } catch (_: Exception) {
                    CheckStatus.OK
                }
                list.add(
                    ChecklistItem(
                        id = obj.optString("id", "c_$i"),
                        category = obj.optString("category", ""),
                        name = obj.optString("name", ""),
                        status = status,
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    @TypeConverter
    fun fromBudgetList(list: List<BudgetItem>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("type", item.type.name)
            obj.put("description", item.description)
            obj.put("quantity", item.quantity)
            obj.put("unitPrice", item.unitPrice)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toBudgetList(data: String?): List<BudgetItem> {
        if (data.isNullOrBlank()) return emptyList()
        val list = mutableListOf<BudgetItem>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeStr = obj.optString("type", ItemType.PECA.name)
                val type = try {
                    ItemType.valueOf(typeStr)
                } catch (_: Exception) {
                    ItemType.PECA
                }
                list.add(
                    BudgetItem(
                        id = obj.optString("id", "b_$i"),
                        type = type,
                        description = obj.optString("description", ""),
                        quantity = obj.optDouble("quantity", 1.0),
                        unitPrice = obj.optDouble("unitPrice", 0.0)
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }
}
