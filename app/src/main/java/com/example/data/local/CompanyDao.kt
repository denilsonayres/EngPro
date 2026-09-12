package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CompanyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Query("SELECT * FROM company_profile WHERE id = 1 LIMIT 1")
    fun getCompanyProfile(): Flow<CompanyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: CompanyProfile)

    @Update
    suspend fun update(company: CompanyProfile)
}
