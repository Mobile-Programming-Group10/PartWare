package com.example.partware.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A source spreadsheet row. Keep raw specifications until normalization is introduced. */
@Entity(tableName = "catalog_parts", indices = [Index("category"), Index("mpn")])
data class CatalogPartEntity(
    @PrimaryKey val id: String,
    val category: String,
    val brand: String,
    val model: String,
    val priceCents: Long?,
    val mpn: String?,
    val socket: String?,
    val memoryType: String?,
    val specificationsJson: String
)
