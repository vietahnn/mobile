package com.example.buoi9_cn.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.buoi9_cn.data.model.Lich
import com.example.buoi9_cn.repository.LichRepository

class LichViewModel : ViewModel() {

    private val repository: LichRepository = LichRepository()
    val allLich: LiveData<List<Lich>> = repository.allLich

    fun insert(lich: Lich, onSuccess: (String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        repository.insert(lich, onSuccess, onFailure)
    }

    fun update(lich: Lich, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        repository.update(lich, onSuccess, onFailure)
    }

    fun delete(lich: Lich, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        repository.delete(lich, onSuccess, onFailure)
    }

    fun getLichByDateRange(startDate: Long, endDate: Long): LiveData<List<Lich>> {
        return repository.getLichByDateRange(startDate, endDate)
    }

    fun getLichFromDate(startDate: Long): LiveData<List<Lich>> {
        return repository.getLichFromDate(startDate)
    }
}

