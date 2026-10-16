package com.example.partware.data.model

/** Categories appear on the home screen in this order. */
enum class PartCategory(val title: String) {
    CPU("CPU"), RAM("RAM"), MOTHERBOARD("Motherboard"), GPU("GPU"),
    STORAGE("Storage"), PSU("PSU"), CASE("Case"), CPU_COOLER("CPU cooler"), CASE_FANS("Case fans")
}

/** One category-specific detail, such as Socket: AM5. */
data class PartSpecification(val label: String, val value: String)

/** Shared catalog fields. USD prices use cents to avoid floating-point rounding. */
data class Part(
    val id: String,
    val brand: String,
    val model: String,
    val category: PartCategory,
    val priceCents: Long,
    val stock: Int,
    val specifications: List<PartSpecification> = emptyList()
)
