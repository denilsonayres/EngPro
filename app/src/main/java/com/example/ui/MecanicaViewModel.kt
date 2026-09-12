package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BudgetItem
import com.example.data.model.CheckStatus
import com.example.data.model.CompanyProfile
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceOrder
import com.example.data.repository.MecanicaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MecanicaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MecanicaRepository

    val companyProfile: StateFlow<CompanyProfile?>
    val allOrders: StateFlow<List<ServiceOrder>>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<OrderStatus?>(null)
    val statusFilter: StateFlow<OrderStatus?> = _statusFilter.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<Long?>(null)
    val selectedOrderId: StateFlow<Long?> = _selectedOrderId.asStateFlow()

    val selectedOrder: StateFlow<ServiceOrder?>

    // Portal do Cliente
    private val _portalTrackingQuery = MutableStateFlow("")
    val portalTrackingQuery: StateFlow<String> = _portalTrackingQuery.asStateFlow()

    private val _portalSearchedOrder = MutableStateFlow<ServiceOrder?>(null)
    val portalSearchedOrder: StateFlow<ServiceOrder?> = _portalSearchedOrder.asStateFlow()

    private val _portalError = MutableStateFlow<String?>(null)
    val portalError: StateFlow<String?> = _portalError.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MecanicaRepository(database.serviceOrderDao(), database.companyDao())

        companyProfile = repository.companyProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allOrders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        selectedOrder = _selectedOrderId.flatMapLatest { id ->
            if (id != null) repository.getOrderById(id) else flowOf(null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Prepopulate demo data on first startup
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    val filteredOrders: StateFlow<List<ServiceOrder>> = combine(
        allOrders,
        _searchQuery,
        _statusFilter
    ) { orders, query, filter ->
        orders.filter { order ->
            val matchesQuery = query.isBlank() ||
                    order.orderCode.contains(query, ignoreCase = true) ||
                    order.clientName.contains(query, ignoreCase = true) ||
                    order.equipmentType.contains(query, ignoreCase = true) ||
                    order.equipmentBrand.contains(query, ignoreCase = true) ||
                    order.equipmentModel.contains(query, ignoreCase = true)

            val matchesStatus = filter == null || order.status == filter.code
            matchesQuery && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: OrderStatus?) {
        _statusFilter.value = filter
    }

    fun selectOrder(id: Long?) {
        _selectedOrderId.value = id
    }

    fun createOrder(order: ServiceOrder, onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val newId = repository.saveOrder(order)
            _selectedOrderId.value = newId
            onCreated(newId)
        }
    }

    fun updateOrder(order: ServiceOrder) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId }
            if (order != null) {
                repository.updateOrder(
                    order.copy(
                        status = newStatus.code,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            if (_selectedOrderId.value == orderId) {
                _selectedOrderId.value = null
            }
        }
    }

    fun updateCompany(company: CompanyProfile) {
        viewModelScope.launch {
            repository.updateCompany(company)
        }
    }

    fun addBudgetItem(orderId: Long, item: BudgetItem) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId } ?: return@launch
            val updatedOrder = if (item.type == com.example.data.model.ItemType.PECA) {
                order.copy(parts = order.parts + item, updatedAt = System.currentTimeMillis())
            } else {
                order.copy(services = order.services + item, updatedAt = System.currentTimeMillis())
            }
            repository.updateOrder(updatedOrder)
        }
    }

    fun removeBudgetItem(orderId: Long, itemId: String) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId } ?: return@launch
            val updatedOrder = order.copy(
                parts = order.parts.filterNot { it.id == itemId },
                services = order.services.filterNot { it.id == itemId },
                updatedAt = System.currentTimeMillis()
            )
            repository.updateOrder(updatedOrder)
        }
    }

    fun updateChecklistItem(orderId: Long, checkId: String, status: CheckStatus, note: String) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId } ?: return@launch
            val updatedList = order.entryChecklist.map { item ->
                if (item.id == checkId) item.copy(status = status, note = note) else item
            }
            repository.updateOrder(order.copy(entryChecklist = updatedList, updatedAt = System.currentTimeMillis()))
        }
    }

    fun addPhotoToOrder(orderId: Long, isExitPhoto: Boolean, photoUri: String) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId } ?: return@launch
            val updatedOrder = if (isExitPhoto) {
                order.copy(exitPhotos = order.exitPhotos + photoUri, updatedAt = System.currentTimeMillis())
            } else {
                order.copy(entryPhotos = order.entryPhotos + photoUri, updatedAt = System.currentTimeMillis())
            }
            repository.updateOrder(updatedOrder)
        }
    }

    // Portal do Cliente actions
    fun setPortalQuery(query: String) {
        _portalTrackingQuery.value = query
        _portalError.value = null
    }

    fun searchPortalOrder() {
        val q = _portalTrackingQuery.value.trim()
        if (q.isBlank()) {
            _portalError.value = "Por favor, digite o código da Ordem de Serviço (Ex: OS-2024-101)."
            return
        }

        val found = allOrders.value.find {
            it.orderCode.equals(q, ignoreCase = true) ||
                    it.orderCode.replace("-", "").equals(q.replace("-", ""), ignoreCase = true)
        }

        if (found != null) {
            _portalSearchedOrder.value = found
            _portalError.value = null
        } else {
            _portalSearchedOrder.value = null
            _portalError.value = "Nenhum equipamento encontrado para o código \"$q\". Verifique o código no seu comprovante."
        }
    }

    fun selectOrderInPortal(order: ServiceOrder) {
        _portalTrackingQuery.value = order.orderCode
        _portalSearchedOrder.value = order
        _portalError.value = null
    }
}
