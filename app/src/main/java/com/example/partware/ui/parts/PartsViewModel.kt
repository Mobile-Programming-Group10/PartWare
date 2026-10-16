package com.example.partware.ui.parts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.data.repository.InMemoryPartsRepository
import com.example.partware.data.repository.PartsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Null category means home; a selected part means its details are open. */
data class PartsUiState(
    val selectedCategory: PartCategory? = null,
    val selectedPart: Part? = null,
    val query: String = "",
    val parts: List<Part> = emptyList()
)

/** Owns navigation and search state, surviving Activity recreation such as rotation. */
class PartsViewModel(repository: PartsRepository) : ViewModel() {
    private val allParts = repository.getParts()
    private val _uiState = MutableStateFlow(PartsUiState())
    // The screen observes state and sends events back through the functions below.
    val uiState = _uiState.asStateFlow()

    /** Opens a category with its inventory and an empty search field. */
    fun openCategory(category: PartCategory) {
        _uiState.value = PartsUiState(selectedCategory = category, parts = filterParts(category, ""))
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
        val category = _uiState.value.selectedCategory ?: return
        _uiState.value = _uiState.value.copy(query = query, parts = filterParts(category, query))
    }

    /** Prevents matching items from unrelated categories when searching AMD, DDR5, etc. */
    private fun filterParts(category: PartCategory, query: String): List<Part> {
        val search = query.trim()
        return allParts.filter { part ->
            part.category == category && (
                part.brand.contains(search, ignoreCase = true) ||
                    part.model.contains(search, ignoreCase = true) ||
                    part.specifications.any { it.value.contains(search, ignoreCase = true) }
                )
        }
    }

    companion object {
        /** Supplies sample inventory to the lifecycle-managed ViewModel. */
        val Factory = viewModelFactory {
            initializer { PartsViewModel(InMemoryPartsRepository()) }
        }
    }
}
