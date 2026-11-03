package com.example.partware.ui.parts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.example.partware.data.database.PartWareDatabase
import com.example.partware.data.repository.RoomPartsRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.data.repository.PartsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Null category means home; a selected part means its details are open. */
data class PartsUiState(
    val selectedCategory: PartCategory? = null,
    val selectedPart: Part? = null,
    val query: String = "",
    // select filter vals
    val filters: Map<String, String> = emptyMap(),
    // dropdown choices
    val filterOptions: Map<String, List<String>> = emptyMap(),
    val parts: List<Part> = emptyList()
)

/** Owns navigation and search state, surviving Activity recreation such as rotation. */
class PartsViewModel(repository: PartsRepository) : ViewModel() {
    private var allParts = repository.getParts()
    private val _uiState = MutableStateFlow(PartsUiState())
    // The screen observes state and sends events back through the functions below.
    val uiState = _uiState.asStateFlow()

    init {
        // Only the Room repository requires asynchronous loading. In-memory unit tests stay synchronous.
        if (repository is RoomPartsRepository) viewModelScope.launch {
            val loaded = repository.loadParts()
            allParts = loaded
            val current = _uiState.value
            current.selectedCategory?.let { category ->
                _uiState.value = current.copy(
                    parts = filterParts(category, current.query, current.filters),
                    filterOptions = buildFilterOptions(category)
                )
            }
        }
    }

    /** Opens a category with its inventory and an empty search field. */
    fun openCategory(category: PartCategory) {
        _uiState.value = PartsUiState(selectedCategory = category, parts = filterParts(category, "", emptyMap()), filterOptions = buildFilterOptions(category))
    }

    /** Opens details only for an item in the displayed category list. */
    fun openPart(partId: String) {
        val part = _uiState.value.parts.find { it.id == partId } ?: return
        _uiState.value = _uiState.value.copy(selectedPart = part)
    }

    /** Returns from details to the filtered list, or from the category list to home. */
    fun goBack() {
        val current = _uiState.value
        _uiState.value = if (current.selectedPart != null) current.copy(selectedPart = null)
            else PartsUiState()
    }

    /** Searches brand, model and specifications inside the selected category. */
    fun onQueryChange(query: String) {
        val current = _uiState.value
        val category = _uiState.value.selectedCategory ?: return

        _uiState.value = current.copy(query = query, parts = filterParts(category, query, current.filters))
    }

    /** Prevents matching items from unrelated categories when searching AMD, DDR5, etc. */
    private fun filterParts(category: PartCategory, query: String, filters: Map<String, String>): List<Part> {
        val search = query.trim()

        return allParts.filter { part ->
            val matchesCategory = part.category == category

            val matchesSearch =
                part.brand.contains(search, ignoreCase = true) || part.model.contains(search, ignoreCase =  true) || part.specifications.any { it.value.contains(search, ignoreCase = true) }

            val matchesFilters = filters.all { (field, selectedValue) ->
                filterValue(part, field)
                    .equals(selectedValue, ignoreCase = true)
            }

            matchesCategory && matchesSearch && matchesFilters
        }
    }

    // Filter control defs
    private fun filterFields(category: PartCategory): List<String> =
        when (category) {
            PartCategory.CPU -> listOf("Brand", "Cores")
            PartCategory.RAM -> listOf("Size", "Memory type", "Speed")
            PartCategory.GPU -> listOf("Brand", "VRAM")
            PartCategory.MOTHERBOARD -> listOf("Socket")
            else-> emptyList()
        }

    // Read stored specific w/database label
    private fun specification(part: Part, label: String): String =
        part.specifications
            .firstOrNull { it.label.equals(label, ignoreCase = true) }
            ?.value
            ?.trim()
            .orEmpty()


    // filter label into product val
    private fun filterValue(part: Part, field: String): String =
        when(field) {
            "Brand" -> part.brand.trim()
            "Memory type" ->
                specification(part, "Ram Type").substringBefore("-")
            "Speed" -> specification(part, "Clock")
            else -> specification(part, field)
        }


    // from category not just current
    private fun buildFilterOptions(
        category: PartCategory
    ): Map<String, List<String>> {
        val categoryParts = allParts.filter { it.category == category }

        return filterFields(category).associateWith {field ->
            categoryParts
                .map { filterValue(it, field) }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase() }
                .sortedWith(
                    compareBy<String> {
                        it.substringBefore(" ").toIntOrNull() ?: Int.MAX_VALUE
                    }.thenBy { it.lowercase() }
                )
        }
    }


    // dropdown select
    fun onFilterChange(field: String, value: String?) {
        val current = _uiState.value
        val category = current.selectedCategory ?: return

        val filters = current.filters.toMutableMap()

        if (value == null) {
            filters.remove(field)
        } else {
            filters[field] = value
        }

        _uiState.value = current.copy(
            filters = filters,
            parts = filterParts(category, current.query, filters)
        )
    }




    companion object {
        /** Supplies sample inventory to the lifecycle-managed ViewModel. */
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    ?: error("Application required for Room")
                PartsViewModel(RoomPartsRepository(PartWareDatabase.get(app).catalogPartDao()))
            }
        }
    }
}
