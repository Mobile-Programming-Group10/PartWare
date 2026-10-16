package com.example.partware.ui.parts

import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.data.model.PartSpecification
import com.example.partware.data.repository.PartsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Documents category isolation, specification search and the home/list/details path. */
class PartsViewModelTest {
    private val cpu = Part("cpu", "AMD", "Example Ryzen 7", PartCategory.CPU, 29900, 5,
        listOf(PartSpecification("Socket", "AM5")))
    private val ram = Part("ram", "Demo", "DDR5 kit", PartCategory.RAM, 9900, 10)
    private val repository = object : PartsRepository {
        override fun getParts() = listOf(cpu, ram)
    }

    /** Home starts without a selection, and opening a category limits its inventory. */
    @Test
    fun categorySearchMatchesSpecificationsWithoutIncludingOtherCategories() {
        val viewModel = PartsViewModel(repository)
        assertNull(viewModel.uiState.value.selectedCategory)
        viewModel.openCategory(PartCategory.CPU)
        viewModel.onQueryChange(" am5 ")
        assertEquals(listOf(cpu), viewModel.uiState.value.parts)
        viewModel.onQueryChange("DDR5")
        assertTrue(viewModel.uiState.value.parts.isEmpty())
        viewModel.onQueryChange("")
        assertEquals(listOf(cpu), viewModel.uiState.value.parts)
    }

    /** Back from details preserves search; another Back returns to home. */
    @Test
    fun backNavigatesFromDetailsToListToHome() {
        val viewModel = PartsViewModel(repository)
        viewModel.openCategory(PartCategory.CPU)
        viewModel.onQueryChange("Ryzen")
        viewModel.openPart(cpu.id)
        assertEquals(cpu, viewModel.uiState.value.selectedPart)
        viewModel.goBack()
        assertNull(viewModel.uiState.value.selectedPart)
        assertEquals("Ryzen", viewModel.uiState.value.query)
        viewModel.goBack()
        assertEquals(PartsUiState(), viewModel.uiState.value)
        viewModel.openCategory(PartCategory.RAM)
        assertEquals(listOf(ram), viewModel.uiState.value.parts)
        assertEquals("", viewModel.uiState.value.query)
    }
}
