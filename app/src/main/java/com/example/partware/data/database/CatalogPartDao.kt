package com.example.partware.data.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CatalogPartDao {
    @Query("SELECT * FROM catalog_parts ORDER BY brand, model")
    suspend fun getAll(): List<CatalogPartEntity>

    @Query("SELECT * FROM catalog_parts WHERE category = :category ORDER BY brand, model")
    suspend fun getCategory(category: String): List<CatalogPartEntity>
}
