package com.example.buoi9_cn.ui.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buoi9_cn.R
import com.example.buoi9_cn.databinding.ActivityLichDetailBinding
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class LichDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLichDetailBinding

    companion object {
        // Keys for backward compatibility if needed
        const val EXTRA_LICH_ID = "id"
        const val EXTRA_HO_VA_TEN = "ho_va_ten"
        const val EXTRA_NGAY_GIO = "ngay_gio"
        const val EXTRA_NOI_DUNG = "noi_dung"
        const val EXTRA_LINK_ANH = "link_anh"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLichDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết lịch hẹn"

        // Get data from Bundle
        val bundle = intent.extras
        if (bundle != null) {
            displayLichDetail(bundle)
        } else {
            Toast.makeText(this, "Không có dữ liệu lịch hẹn", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun displayLichDetail(bundle: Bundle) {
        // Using BundleHelper for cleaner code
        val hoVaTen = com.example.buoi9_cn.utils.BundleHelper.getHoVaTen(bundle)
        val ngayGio = com.example.buoi9_cn.utils.BundleHelper.getNgayGio(bundle)
        val noiDung = com.example.buoi9_cn.utils.BundleHelper.getNoiDung(bundle)
        val linkAnh = com.example.buoi9_cn.utils.BundleHelper.getLinkAnh(bundle)

        // Display data
        binding.tvDetailHoVaTen.text = hoVaTen
        binding.tvDetailNoiDung.text = noiDung

        // Format date time
        val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy 'lúc' HH:mm", Locale.forLanguageTag("vi-VN"))
        binding.tvDetailNgayGio.text = dateFormat.format(Date(ngayGio))

        // Calculate time remaining
        val timeRemaining = ngayGio - System.currentTimeMillis()
        if (timeRemaining > 0) {
            val days = timeRemaining / (24 * 60 * 60 * 1000)
            val hours = (timeRemaining % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
            val minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000)

            binding.tvTimeRemaining.text = when {
                days > 0 -> "Còn $days ngày, $hours giờ nữa"
                hours > 0 -> "Còn $hours giờ, $minutes phút nữa"
                minutes > 0 -> "Còn $minutes phút nữa"
                else -> "Sắp đến giờ hẹn"
            }
        } else {
            binding.tvTimeRemaining.text = "Đã qua"
        }

        // Load image from URL
        if (linkAnh.isNotEmpty()) {
            binding.imgDetailAnh.setImageResource(R.drawable.ic_launcher_foreground)
            thread {
                try {
                    val url = URL(linkAnh)
                    val input: InputStream = url.openStream()
                    val drawable = Drawable.createFromStream(input, "src")
                    runOnUiThread {
                        binding.imgDetailAnh.setImageDrawable(drawable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            binding.imgDetailAnh.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current state
        outState.putString("current_ho_va_ten", binding.tvDetailHoVaTen.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore state if needed
        savedInstanceState.getString("current_ho_va_ten")?.let {
            // Handle restored state
        }
    }
}

