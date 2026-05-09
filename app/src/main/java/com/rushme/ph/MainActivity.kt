package com.rushme.ph

import android.os.Bundle
import android.util.Log // Idinagdag para sa debugging
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rushme.ph.ui.theme.RushmeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RushmeTheme {
                var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    try {
                        productList = ApiService.getInstance().getProducts()
                        // Debug: I-check kung may laman ang imageUrl pagkatapos ng API call
                        productList.forEach {
                            Log.d("RUSHME_DEBUG", "Product: ${it.name}, URL: ${it.imageUrl}")
                        }
                    } catch (e: Exception) {
                        Log.e("RUSHME_DEBUG", "API Error: ${e.message}")
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (isLoading) {
                        Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        RushmeMainScreen(productList)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RushmeMainScreen(products: List<Product>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RUSHME PH", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6750A4),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                // Idinagdag ang OnError para mahuli ang rason ng pagkabigo
                onError = { error ->
                    Log.e("RUSHME_IMAGE_ERROR", "Failed to load: ${product.imageUrl}")
                    Log.e("RUSHME_IMAGE_ERROR", "Reason: ${error.result.throwable.message}")
                }
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("₱${product.price}", color = Color(0xFFE91E63), fontWeight = FontWeight.Black)
                Text(product.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}