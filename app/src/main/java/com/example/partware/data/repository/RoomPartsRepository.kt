package com.example.partware.data.repository

import com.example.partware.data.database.CatalogPartDao
import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.data.model.PartSpecification
import org.json.JSONObject

/** Room is queried asynchronously from the ViewModel; this adapter retains the UI model. */
class RoomPartsRepository(private val dao: CatalogPartDao) : PartsRepository {
    override fun getParts(): List<Part> = emptyList() // async catalog loads through loadParts()

    override suspend fun loadParts(): List<Part> = dao.getAll().mapNotNull { record ->
        val category = PartCategory.entries.find { it.name == record.category } ?: return@mapNotNull null
        val json = JSONObject(record.specificationsJson)
        val specs = mutableListOf<PartSpecification>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            specs.add(PartSpecification(key, json.optString(key)))
        }
        Part(
            id = record.id,
            brand = record.brand,
            model = record.model,
            category = category,
            priceCents = record.priceCents,
            stock = -1, // retailer availability not yet integrated
            specifications = specs
        )
    }
}
