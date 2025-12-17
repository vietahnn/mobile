package com.example.buoi9_cn.ui.view

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buoi9_cn.databinding.ActivityMainBinding
import com.example.buoi9_cn.databinding.DialogAddLichBinding
import com.example.buoi9_cn.data.model.Lich
import com.example.buoi9_cn.ui.adapter.LichAdapter
import com.example.buoi9_cn.ui.viewmodel.LichViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LichViewModel
    private lateinit var adapter: LichAdapter

    private var startDateMillis: Long = 0
    private var endDateMillis: Long = Long.MAX_VALUE

    companion object {
        private const val KEY_START_DATE = "start_date_millis"
        private const val KEY_END_DATE = "end_date_millis"
        private const val KEY_START_DATE_TEXT = "start_date_text"
        private const val KEY_END_DATE_TEXT = "end_date_text"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore state if available
        savedInstanceState?.let {
            startDateMillis = it.getLong(KEY_START_DATE, 0)
            endDateMillis = it.getLong(KEY_END_DATE, Long.MAX_VALUE)
            binding.edtStartDate.setText(it.getString(KEY_START_DATE_TEXT, ""))
            binding.edtEndDate.setText(it.getString(KEY_END_DATE_TEXT, ""))
        }

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setupRecyclerView()
        setupViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = LichAdapter(
            lichList = emptyList(),
            onItemClick = { lich ->
                showDeleteConfirmDialog(lich)
            },
            onItemLongClick = { lich ->
                openLichDetail(lich)
            }
        )
        binding.rvLich.adapter = adapter
        binding.rvLich.layoutManager = LinearLayoutManager(this)
    }

    private fun openLichDetail(lich: Lich) {
        val intent = Intent(this, LichDetailActivity::class.java)
        // Using BundleHelper for cleaner code
        val bundle = com.example.buoi9_cn.utils.BundleHelper.lichToBundle(lich)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LichViewModel::class.java]
        viewModel.allLich.observe(this) { lichList ->
            lichList?.let {
                // Add sample data if list is empty
                if (it.isEmpty()) {
                    addSampleData()
                }
                filterAndUpdateList(it)
            }
        }
    }

    private fun addSampleData() {
        val calendar = Calendar.getInstance()

        // Sample 1: Meeting tomorrow at 10:00
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        val lich1 = Lich(
            hoVaTen = "Nguyễn Văn A",
            ngayGio = calendar.timeInMillis,
            noiDung = "Họp dự án phát triển ứng dụng mobile",
            linkAnh = "https://i.pravatar.cc/150?img=1"
        )
        viewModel.insert(lich1)
        scheduleNotification(lich1)

        // Sample 2: Appointment in 2 days at 14:30
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 30)
        val lich2 = Lich(
            hoVaTen = "Trần Thị B",
            ngayGio = calendar.timeInMillis,
            noiDung = "Gặp khách hàng thảo luận về thiết kế UI/UX",
            linkAnh = "https://i.pravatar.cc/150?img=5"
        )
        viewModel.insert(lich2)
        scheduleNotification(lich2)

        // Sample 3: Event in 3 days at 16:00
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 0)
        val lich3 = Lich(
            hoVaTen = "Lê Văn C",
            ngayGio = calendar.timeInMillis,
            noiDung = "Presentation về kết quả nghiên cứu thị trường",
            linkAnh = "https://i.pravatar.cc/150?img=8"
        )
        viewModel.insert(lich3)
        scheduleNotification(lich3)

        // Sample 4: Meeting next week at 9:00
        calendar.add(Calendar.DAY_OF_MONTH, 4)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        val lich4 = Lich(
            hoVaTen = "Phạm Thị D",
            ngayGio = calendar.timeInMillis,
            noiDung = "Review code và testing ứng dụng",
            linkAnh = "https://i.pravatar.cc/150?img=9"
        )
        viewModel.insert(lich4)
        scheduleNotification(lich4)

        // Sample 5: Another meeting at 15:00 same day as sample 4
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        val lich5 = Lich(
            hoVaTen = "Hoàng Văn E",
            ngayGio = calendar.timeInMillis,
            noiDung = "Đào tạo nhân viên mới về quy trình làm việc",
            linkAnh = "https://i.pravatar.cc/150?img=12"
        )
        viewModel.insert(lich5)
        scheduleNotification(lich5)
    }

    private fun setupClickListeners() {
        binding.edtStartDate.setOnClickListener {
            showDatePickerDialog { date ->
                binding.edtStartDate.setText(date)
                startDateMillis = parseDateToMillis(date)
                viewModel.allLich.value?.let { filterAndUpdateList(it) }
            }
        }

        binding.edtEndDate.setOnClickListener {
            showDatePickerDialog { date ->
                binding.edtEndDate.setText(date)
                endDateMillis = parseDateToMillis(date) + (24 * 60 * 60 * 1000) - 1 // End of day
                viewModel.allLich.value?.let { filterAndUpdateList(it) }
            }
        }

        binding.btThemLich.setOnClickListener {
            showAddLichDialog()
        }
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                calendar.set(year, month, day)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun parseDateToMillis(dateString: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.parse(dateString)?.time ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun filterAndUpdateList(lichList: List<Lich>) {
        val filteredList = lichList.filter { lich ->
            lich.ngayGio in startDateMillis..endDateMillis
        }
        adapter.updateList(filteredList)
    }

    private fun showAddLichDialog() {
        val dialogBinding = DialogAddLichBinding.inflate(layoutInflater)
        var selectedDateTimeMillis: Long = 0

        dialogBinding.edtNgayGio.setOnClickListener {
            showDateTimePickerDialog { dateTimeString, millis ->
                dialogBinding.edtNgayGio.setText(dateTimeString)
                selectedDateTimeMillis = millis
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Thêm Lịch Hẹn")
            .setView(dialogBinding.root)
            .setPositiveButton("Thêm") { _, _ ->
                val hoVaTen = dialogBinding.edtHoVaTen.text.toString().trim()
                val noiDung = dialogBinding.edtNoidung.text.toString().trim()
                val linkAnh = dialogBinding.edtLinkAnh.text.toString().trim()
                val ngayGio = dialogBinding.edtNgayGio.text.toString().trim()

                if (hoVaTen.isEmpty() || noiDung.isEmpty() || ngayGio.isEmpty()) {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else if (selectedDateTimeMillis <= 0) {
                    Toast.makeText(this, "Vui lòng chọn ngày giờ hợp lệ", Toast.LENGTH_SHORT).show()
                } else {
                    val lich = Lich(
                        hoVaTen = hoVaTen,
                        ngayGio = selectedDateTimeMillis,
                        noiDung = noiDung,
                        linkAnh = linkAnh.ifEmpty { "https://via.placeholder.com/150" }
                    )
                    viewModel.insert(lich)
                    scheduleNotification(lich)
                    Toast.makeText(this, "Đã thêm lịch hẹn", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDateTimePickerDialog(onDateTimeSelected: (String, Long) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        onDateTimeSelected(dateFormat.format(calendar.time), calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showDeleteConfirmDialog(lich: Lich) {
        AlertDialog.Builder(this)
            .setTitle("Xóa lịch hẹn")
            .setMessage("Bạn có chắc chắn muốn xóa lịch hẹn với ${lich.hoVaTen}?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.delete(lich)
                cancelNotification(lich)
                Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun scheduleNotification(lich: Lich) {
        val notificationTime = lich.ngayGio - (30 * 60 * 1000) // 30 minutes before

        if (notificationTime <= System.currentTimeMillis()) {
            return // Don't schedule notification for past events
        }

        val intent = Intent(this, NotificationReceiver::class.java)
        val bundle = Bundle().apply {
            putString(NotificationReceiver.EXTRA_HO_VA_TEN, lich.hoVaTen)
            putString(NotificationReceiver.EXTRA_NOI_DUNG, lich.noiDung)
            putString(NotificationReceiver.EXTRA_LICH_ID, lich.id)
        }
        intent.putExtras(bundle)

        val requestCode = lich.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        }
    }

    private fun cancelNotification(lich: Lich) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val requestCode = lich.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save filter dates
        outState.putLong(KEY_START_DATE, startDateMillis)
        outState.putLong(KEY_END_DATE, endDateMillis)
        outState.putString(KEY_START_DATE_TEXT, binding.edtStartDate.text.toString())
        outState.putString(KEY_END_DATE_TEXT, binding.edtEndDate.text.toString())
    }
}