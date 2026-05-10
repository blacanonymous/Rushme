package com.rushme.ph

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rushme.ph.ui.theme.RushmeTheme
import kotlinx.coroutines.launch

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
                        Log.d("RUSHME_DEBUG", "Data loaded: ${productList.size} items")
                    } catch (e: Exception) {
                        Log.e("RUSHME_DEBUG", "API Error: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5)
                ) {
                    if (isLoading) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFFD32F2F))
                        }
                    } else {
                        // Ipinapasa natin ang productList sa Main Screen
                        RushmeMainScreen(productList)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RushmeMainScreen(initialProducts: List<Product>) {
    var searchQuery by remember { mutableStateOf("") }
    // Ginagawa nating mutable ang listahan para ma-update pag nag-refresh
    var products by remember { mutableStateOf(initialProducts) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    // Logic para sa Search
    val filteredProducts = products.filter {
        it.itemCode.contains(searchQuery, ignoreCase = true) ||
                it.itemDescription.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFFD32F2F))) {
                CenterAlignedTopAppBar(
                    title = { Text("RUSHME BILLIARD BROCHURE", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    placeholder = { Text("Search Item Code or Description...") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { padding ->
        // ITO ANG DINAGDAG PARA SA SCROLL DOWN REFRESH
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    try {
                        val newProducts = ApiService.getInstance().getProducts()
                        products = newProducts
                    } catch (e: Exception) {
                        Log.e("RUSHME_REFRESH", "Error: ${e.message}")
                    } finally {
                        isRefreshing = false
                    }
                }
            },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                onError = { Log.e("IMAGE_ERROR", "Failed to load image for ${product.itemCode}") }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.itemCode,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = product.itemDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
                Text(
                    text = "Category: ${product.subCategory}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₱${product.netPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}