package com.example.partware.data.repository

import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.data.model.PartSpecification

/** Supplies inventory independently of the screen and its ViewModel. */
interface PartsRepository {
    /** Returns the catalog from the current data source. */
    fun getParts(): List<Part>
    /** Async override for Room. In-memory tests retain synchronous behavior. */
    suspend fun loadParts(): List<Part> = getParts()
}

/** Illustrative products, prices and specifications for UI development; not a real catalog. */
class InMemoryPartsRepository : PartsRepository {
    /** Provides examples in all nine categories so every tile opens useful sample content. */
    override fun getParts(): List<Part> = listOf(
        Part("cpu-amd", "AMD", "Example Ryzen 7 CPU", PartCategory.CPU, 29900, 5,
            specs("Family" to "Ryzen", "Tier" to "7", "Socket" to "AM5", "Variant" to "X3D",
                "Architecture" to "Example Zen architecture", "Cores" to "8", "Threads" to "16",
                "Base clock" to "4.0 GHz", "Boost clock" to "5.0 GHz")),
        Part("cpu-intel", "Intel", "Example Core Ultra 5 CPU", PartCategory.CPU, 24900, 3,
            specs("Family" to "Core Ultra", "Tier" to "5", "Variant" to "K",
                "Socket" to "Example Intel socket", "Cores" to "14", "Threads" to "14",
                "Base clock" to "3.5 GHz", "Boost clock" to "5.0 GHz")),
        Part("ram-ddr5", "Demo brand", "Example DDR5 kit", PartCategory.RAM, 9900, 10,
            specs("Memory type" to "DDR5", "Total capacity" to "32 GB", "Kit" to "2 × 16 GB",
                "Speed" to "6000 MT/s", "CAS latency" to "CL30", "Profiles" to "AMD EXPO")),
        Part("ram-ddr4", "Demo brand", "Example DDR4 kit", PartCategory.RAM, 4900, 8,
            specs("Memory type" to "DDR4", "Total capacity" to "16 GB", "Kit" to "2 × 8 GB",
                "Speed" to "3200 MT/s", "CAS latency" to "CL16", "Profiles" to "Intel XMP")),
        Part("mobo", "Demo brand", "Example AM5 motherboard", PartCategory.MOTHERBOARD, 17900, 4,
            specs("Socket" to "AM5", "Chipset" to "B650", "Form factor" to "ATX",
                "Memory type" to "DDR5", "RAM slots" to "4", "M.2 slots" to "2")),
        Part("gpu", "Demo brand", "Example graphics card", PartCategory.GPU, 39900, 2,
            specs("VRAM" to "12 GB", "Memory type" to "GDDR6", "Length" to "280 mm",
                "Power" to "200 W", "Power connector" to "1 × 8-pin")),
        Part("storage", "Demo brand", "Example NVMe SSD", PartCategory.STORAGE, 7900, 12,
            specs("Type" to "SSD", "Capacity" to "1 TB", "Form factor" to "M.2 2280",
                "Interface" to "PCIe 4.0 / NVMe", "Sequential read" to "7000 MB/s",
                "Sequential write" to "5000 MB/s")),
        Part("psu", "Demo brand", "Example 750 W power supply", PartCategory.PSU, 10900, 6,
            specs("Form factor" to "ATX", "Wattage" to "750 W", "Efficiency" to "80 PLUS Gold",
                "Cabling" to "Fully modular")),
        Part("case", "Demo brand", "Example mid tower", PartCategory.CASE, 8900, 3,
            specs("Motherboard support" to "ATX / Micro-ATX / Mini-ITX", "GPU clearance" to "360 mm",
                "CPU cooler clearance" to "165 mm", "PSU support" to "ATX")),
        Part("cooler", "Demo brand", "Example tower cooler", PartCategory.CPU_COOLER, 3900, 7,
            specs("Type" to "Air", "Sockets" to "AM4 / AM5", "Height" to "155 mm", "Fan size" to "120 mm")),
        Part("fans", "Demo brand", "Example PWM fan", PartCategory.CASE_FANS, 1500, 0,
            specs("Size" to "120 mm", "Thickness" to "25 mm", "Speed" to "500–1800 RPM",
                "Connector" to "4-pin PWM", "Lighting" to "None"))
    )

    /** Converts label/value pairs into rows for the category-specific details screen. */
    private fun specs(vararg values: Pair<String, String>): List<PartSpecification> =
        values.map { (label, value) -> PartSpecification(label, value) }
}
