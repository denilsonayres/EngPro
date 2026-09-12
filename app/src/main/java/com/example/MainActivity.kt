package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MecanicaViewModel
import com.example.ui.screens.CompanyProfileScreen
import com.example.ui.screens.CustomerPortalScreen
import com.example.ui.screens.NewOrderScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.WorkshopDashboardScreen
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.MecanicaTheme
import com.example.ui.theme.Slate500

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MecanicaTheme {
                MecanicaApp()
            }
        }
    }
}

@Composable
fun MecanicaApp(viewModel: MecanicaViewModel = viewModel()) {
    var currentNavTab by remember { mutableIntStateOf(0) }
    var isCreatingOrder by remember { mutableStateOf(false) }

    val filteredOrders by viewModel.filteredOrders.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val company by viewModel.companyProfile.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()

    val portalTrackingQuery by viewModel.portalTrackingQuery.collectAsStateWithLifecycle()
    val portalSearchedOrder by viewModel.portalSearchedOrder.collectAsStateWithLifecycle()
    val portalError by viewModel.portalError.collectAsStateWithLifecycle()

    // Handle system back navigation gracefully
    BackHandler(enabled = isCreatingOrder || selectedOrder != null) {
        if (isCreatingOrder) {
            isCreatingOrder = false
        } else if (selectedOrder != null) {
            viewModel.selectOrder(null)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom navigation when on primary screens
            if (!isCreatingOrder && selectedOrder == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentNavTab == 0,
                        onClick = { currentNavTab = 0 },
                        icon = { Icon(Icons.Default.Engineering, contentDescription = "Oficina") },
                        label = {
                            Text(
                                text = "Oficina (OS)",
                                fontSize = 11.sp,
                                fontWeight = if (currentNavTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndustrialBlue,
                            selectedTextColor = IndustrialBlue,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentNavTab == 1,
                        onClick = { currentNavTab = 1 },
                        icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Portal do Cliente") },
                        label = {
                            Text(
                                text = "Portal Cliente",
                                fontSize = 11.sp,
                                fontWeight = if (currentNavTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F766E),
                            selectedTextColor = Color(0xFF0F766E),
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = Color(0xFFCCFBF1)
                        )
                    )

                    NavigationBarItem(
                        selected = currentNavTab == 2,
                        onClick = { currentNavTab = 2 },
                        icon = { Icon(Icons.Default.Business, contentDescription = "SaaS & Oficina") },
                        label = {
                            Text(
                                text = "SaaS Oficina",
                                fontSize = 11.sp,
                                fontWeight = if (currentNavTab == 2) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndustrialBlue,
                            selectedTextColor = IndustrialBlue,
                            unselectedIconColor = Slate500,
                            unselectedTextColor = Slate500,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Sub-screen 1: Create New Reception / Checklist
                isCreatingOrder -> {
                    NewOrderScreen(
                        onNavigateBack = { isCreatingOrder = false },
                        onOrderCreated = { newOrder ->
                            viewModel.createOrder(newOrder) {
                                isCreatingOrder = false
                            }
                        },
                        existingOrdersCount = allOrders.size
                    )
                }

                // Sub-screen 2: Order Detail View
                selectedOrder != null -> {
                    OrderDetailScreen(
                        order = selectedOrder!!,
                        company = company,
                        onNavigateBack = { viewModel.selectOrder(null) },
                        onUpdateStatus = { newStatus ->
                            viewModel.updateOrderStatus(selectedOrder!!.id, newStatus)
                        },
                        onAddBudgetItem = { newItem ->
                            viewModel.addBudgetItem(selectedOrder!!.id, newItem)
                        },
                        onRemoveBudgetItem = { itemId ->
                            viewModel.removeBudgetItem(selectedOrder!!.id, itemId)
                        },
                        onAddPhoto = { isExit, uri ->
                            viewModel.addPhotoToOrder(selectedOrder!!.id, isExit, uri)
                        },
                        onUpdateOrder = { updated ->
                            viewModel.updateOrder(updated)
                        }
                    )
                }

                // Main Tab 0: Workshop Dashboard
                currentNavTab == 0 -> {
                    WorkshopDashboardScreen(
                        orders = filteredOrders,
                        allOrdersCount = allOrders.size,
                        company = company,
                        searchQuery = searchQuery,
                        onSearchChange = viewModel::setSearchQuery,
                        selectedFilter = statusFilter,
                        onFilterSelect = viewModel::setStatusFilter,
                        onOrderClick = { orderId -> viewModel.selectOrder(orderId) },
                        onNewOrderClick = { isCreatingOrder = true }
                    )
                }

                // Main Tab 1: Customer Portal
                currentNavTab == 1 -> {
                    CustomerPortalScreen(
                        searchedOrder = portalSearchedOrder,
                        searchQuery = portalTrackingQuery,
                        onQueryChange = viewModel::setPortalQuery,
                        onSearchClick = viewModel::searchPortalOrder,
                        onSelectQuickOrder = viewModel::selectOrderInPortal,
                        allOrders = allOrders,
                        company = company,
                        errorMessage = portalError
                    )
                }

                // Main Tab 2: SaaS Profile & Workshop Settings
                currentNavTab == 2 -> {
                    CompanyProfileScreen(
                        company = company,
                        onSaveCompany = viewModel::updateCompany
                    )
                }
            }
        }
    }
}
