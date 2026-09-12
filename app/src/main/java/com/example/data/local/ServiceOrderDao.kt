package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ServiceOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceOrderDao {

    @Query("SELECT * FROM service_orders ORDER BY updatedAt DESC")
    fun getAllOrders(): Flow<List<ServiceOrder>>

    @Query("SELECT * FROM service_orders WHERE id = :id")
    fun getOrderById(id: Long): Flow<ServiceOrder?>

    @Query("SELECT * FROM service_orders WHERE orderCode = :code LIMIT 1")
    fun getOrderByCode(code: String): Flow<ServiceOrder?>

    @Query("SELECT * FROM service_orders WHERE orderCode LIKE '%' || :query || '%' OR clientName LIKE '%' || :query || '%' OR equipmentType LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchOrders(query: String): Flow<List<ServiceOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: ServiceOrder): Long

    @Update
    suspend fun update(order: ServiceOrder)

    @Delete
    suspend fun delete(order: ServiceOrder)

    @Query("DELETE FROM service_orders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM service_orders")
    suspend fun getCount(): Int
}
