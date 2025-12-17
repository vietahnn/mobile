package com.example.buoi9_cn.data.model

data class Lich(
    var id: String = "",
    val hoVaTen: String = "",
    val ngayGio: Long = 0, // Timestamp in milliseconds
    val noiDung: String = "",
    val linkAnh: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0, "", "")
}

