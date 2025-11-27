package com.example.bt_canhan_tuan_7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bt_canhan_tuan_7.data.entity.Appointment
import com.example.bt_canhan_tuan_7.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AppointmentViewModel(private val repository: AppointmentRepository) : ViewModel() {
    
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _filteredAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val filteredAppointments: StateFlow<List<Appointment>> = _filteredAppointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllAppointments()
    }

    fun loadAllAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllAppointments().collect { appointments ->
                    _appointments.value = appointments
                    _filteredAppointments.value = appointments
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading appointments: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun filterAppointmentsByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime) {
        viewModelScope.launch {
            try {
                repository.getAppointmentsByTimeRange(startTime, endTime).collect { appointments ->
                    _filteredAppointments.value = appointments
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error filtering appointments: ${e.message}"
            }
        }
    }

    fun addAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.insertAppointment(appointment)
                loadAllAppointments()
            } catch (e: Exception) {
                _errorMessage.value = "Error adding appointment: ${e.message}"
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.deleteAppointment(appointment)
                loadAllAppointments()
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting appointment: ${e.message}"
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.updateAppointment(appointment)
                loadAllAppointments()
            } catch (e: Exception) {
                _errorMessage.value = "Error updating appointment: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetFilter() {
        _filteredAppointments.value = _appointments.value
    }
}
