package com.example.buoi9_cn.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.buoi9_cn.data.model.Lich
import com.google.firebase.database.*

class LichRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("lich")
    private val _allLich = MutableLiveData<List<Lich>>()
    val allLich: LiveData<List<Lich>> = _allLich

    init {
        // Listen for changes in Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lichList = mutableListOf<Lich>()
                for (lichSnapshot in snapshot.children) {
                    val lich = lichSnapshot.getValue(Lich::class.java)
                    lich?.let {
                        it.id = lichSnapshot.key ?: ""
                        lichList.add(it)
                    }
                }
                // Sort by date
                lichList.sortBy { it.ngayGio }
                _allLich.value = lichList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun insert(lich: Lich, onSuccess: (String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val key = database.push().key ?: return
        lich.id = key
        database.child(key).setValue(lich)
            .addOnSuccessListener { onSuccess(key) }
            .addOnFailureListener { onFailure(it) }
    }

    fun update(lich: Lich, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        if (lich.id.isEmpty()) return
        database.child(lich.id).setValue(lich)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun delete(lich: Lich, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        if (lich.id.isEmpty()) return
        database.child(lich.id).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getLichByDateRange(startDate: Long, endDate: Long): LiveData<List<Lich>> {
        val filteredLich = MutableLiveData<List<Lich>>()
        database.orderByChild("ngayGio")
            .startAt(startDate.toDouble())
            .endAt(endDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lichList = mutableListOf<Lich>()
                    for (lichSnapshot in snapshot.children) {
                        val lich = lichSnapshot.getValue(Lich::class.java)
                        lich?.let {
                            it.id = lichSnapshot.key ?: ""
                            lichList.add(it)
                        }
                    }
                    lichList.sortBy { it.ngayGio }
                    filteredLich.value = lichList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        return filteredLich
    }

    fun getLichFromDate(startDate: Long): LiveData<List<Lich>> {
        val filteredLich = MutableLiveData<List<Lich>>()
        database.orderByChild("ngayGio")
            .startAt(startDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lichList = mutableListOf<Lich>()
                    for (lichSnapshot in snapshot.children) {
                        val lich = lichSnapshot.getValue(Lich::class.java)
                        lich?.let {
                            it.id = lichSnapshot.key ?: ""
                            lichList.add(it)
                        }
                    }
                    lichList.sortBy { it.ngayGio }
                    filteredLich.value = lichList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        return filteredLich
    }
}

