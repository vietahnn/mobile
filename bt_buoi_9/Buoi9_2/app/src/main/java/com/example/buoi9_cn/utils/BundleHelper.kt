package com.example.buoi9_cn.utils

import android.os.Bundle
import com.example.buoi9_cn.data.model.Lich

/**
 * Helper object for converting Lich model to/from Bundle
 * Demonstrates best practices for using Bundle with custom objects
 */
object BundleHelper {

    private const val KEY_ID = "id"
    private const val KEY_HO_VA_TEN = "ho_va_ten"
    private const val KEY_NGAY_GIO = "ngay_gio"
    private const val KEY_NOI_DUNG = "noi_dung"
    private const val KEY_LINK_ANH = "link_anh"

    /**
     * Convert Lich object to Bundle
     */
    fun lichToBundle(lich: Lich): Bundle {
        return Bundle().apply {
            putString(KEY_ID, lich.id)
            putString(KEY_HO_VA_TEN, lich.hoVaTen)
            putLong(KEY_NGAY_GIO, lich.ngayGio)
            putString(KEY_NOI_DUNG, lich.noiDung)
            putString(KEY_LINK_ANH, lich.linkAnh)
        }
    }

    /**
     * Convert Bundle to Lich object
     */
    fun bundleToLich(bundle: Bundle): Lich? {
        return try {
            Lich(
                id = bundle.getString(KEY_ID) ?: "",
                hoVaTen = bundle.getString(KEY_HO_VA_TEN) ?: return null,
                ngayGio = bundle.getLong(KEY_NGAY_GIO, 0),
                noiDung = bundle.getString(KEY_NOI_DUNG) ?: return null,
                linkAnh = bundle.getString(KEY_LINK_ANH) ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Put Lich data into existing Bundle
     */
    fun putLichData(bundle: Bundle, lich: Lich) {
        bundle.putString(KEY_ID, lich.id)
        bundle.putString(KEY_HO_VA_TEN, lich.hoVaTen)
        bundle.putLong(KEY_NGAY_GIO, lich.ngayGio)
        bundle.putString(KEY_NOI_DUNG, lich.noiDung)
        bundle.putString(KEY_LINK_ANH, lich.linkAnh)
    }

    /**
     * Get individual values from Bundle
     */
    fun getLichId(bundle: Bundle): String = bundle.getString(KEY_ID) ?: ""
    fun getHoVaTen(bundle: Bundle): String = bundle.getString(KEY_HO_VA_TEN) ?: ""
    fun getNgayGio(bundle: Bundle): Long = bundle.getLong(KEY_NGAY_GIO, 0)
    fun getNoiDung(bundle: Bundle): String = bundle.getString(KEY_NOI_DUNG) ?: ""
    fun getLinkAnh(bundle: Bundle): String = bundle.getString(KEY_LINK_ANH) ?: ""
}
