package com.example.partware.ui.parts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.partware.data.model.Part
import com.example.partware.data.model.PartCategory
import com.example.partware.ui.theme.PartWareTheme
import java.text.NumberFormat
import java.util.Locale

/** Connects lifecycle-aware state and user actions to the screen. */
@Composable
fun PartsRoute(viewModel: PartsViewModel = viewModel(factory = PartsViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Android's Back action follows the same path as the visible Back button.
    BackHandler(enabled = state.selectedCategory != null, onBack = viewModel::goBack)
    PartsScreen(state, viewModel::openCategory, viewModel::openPart,
        viewModel::onQueryChange, viewModel::goBack)
}

/** Keeps the PartWare header visible while switching between home, category and details. */
@Composable
fun PartsScreen(
    state: PartsUiState,
    onCategoryClick: (PartCategory) -> Unit,
    onPartClick: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("PartWare", style = MaterialTheme.typography.headlineLarge)
            if (state.selectedCategory != null) {
                // Return to the category list from details, or home from a list.
                TextButton(onClick = onBack) {
                    Text(if (state.selectedPart != null) "Back to ${state.selectedCategory.title}" else "Back to categories")
                }
            }
            val selectedPart = state.selectedPart
            val category = state.selectedCategory
            when {
                selectedPart != null -> PartDetailsScreen(selectedPart)
                category != null -> CategoryPartsScreen(category, state, onPartClick, onQueryChange)
                else -> CategoryHomeScreen(onCategoryClick)
            }
        }
    }
}

/** Home screen: two columns of category buttons, ordered row by row. */
@Composable
private fun CategoryHomeScreen(onCategoryClick: (PartCategory) -> Unit) {
    Text("Browse parts", style = MaterialTheme.typography.titleMedium)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        gridItems(PartCategory.entries, key = { it.name }) { category ->
            // The entire tile opens this category's inventory list.
            Card(onClick = { onCategoryClick(category) }, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.heightIn(min = 112.dp).padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(category.title, style = MaterialTheme.typography.titleMedium)
                    Text("Browse parts", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/** Category screen: searchable, scrolling inventory with clickable product cards. */
@Composable
private fun CategoryPartsScreen(
    category: PartCategory,
    state: PartsUiState,
    onPartClick: (String) -> Unit,
    onQueryChange: (String) -> Unit
) {
    Text(category.title, style = MaterialTheme.typography.headlineMedium)
    Text("Hardware specifications from the local catalog · prices are historical or unavailable", style = MaterialTheme.typography.bodySmall)
    // Searches only this category, including spec values such as Ryzen, AM5 and DDR5.
    OutlinedTextField(
        value = state.query,
        onValueChange = onQueryChange,
        label = { Text("Search ${category.title}") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    if (state.parts.isEmpty()) {
        Text("No parts match your search.")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.parts, key = { it.id }) { part ->
                // Tap a product to see its category-specific specifications.
                Card(onClick = { onPartClick(part.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(part.model, style = MaterialTheme.typography.titleMedium)
                        Text(part.brand)
                        Text(formatPrice(part.priceCents))
                        StockLabel(part.stock)
                    }
                }
            }
        }
    }
}

/** Detail screen: shared product fields followed by this part's specification rows. */
@Composable
private fun PartDetailsScreen(part: Part) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(part.model, style = MaterialTheme.typography.headlineMedium)
            Text("Imported hardware specifications · prices may be historical")
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ID: ${part.id}")
                    Text("Brand: ${part.brand}")
                    Text("Category: ${part.category.title}")
                    Text("Price: ${formatPrice(part.priceCents)}")
                    StockLabel(part.stock)
                }
            }
        }
        item { Text("Specifications", style = MaterialTheme.typography.titleLarge) }
        items(part.specifications) { specification ->
            Column {
                Text(specification.label, style = MaterialTheme.typography.labelLarge)
                Text(specification.value, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/** Uses explicit wording and color to distinguish sold-out items. */
@Composable
private fun StockLabel(stock: Int) {
    Text(
        text = if (stock < 0) "Availability unknown" else if (stock == 0) "Out of stock" else "In stock: $stock",
        color = if (stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    )
}

/** Formats integer cents consistently as USD for this starter catalog. */
private fun formatPrice(cents: Long?): String =
    cents?.let { NumberFormat.getCurrencyInstance(Locale.US).format(java.math.BigDecimal.valueOf(it, 2)) } ?: "Price unavailable"

/** Allows the category grid to be inspected in Android Studio without running the app. */
@Preview(showBackground = true)
@Composable
private fun PartsHomePreview() {
    PartWareTheme {
        PartsScreen(PartsUiState(), {}, {}, {}, {})
    }
}
