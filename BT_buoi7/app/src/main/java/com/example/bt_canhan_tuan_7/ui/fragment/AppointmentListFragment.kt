package com.example.bt_canhan_tuan_7.ui.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bt_canhan_tuan_7.R
import com.example.bt_canhan_tuan_7.data.database.AppointmentDatabase
import com.example.bt_canhan_tuan_7.data.repository.AppointmentRepository
import com.example.bt_canhan_tuan_7.databinding.FragmentAppointmentListBinding
import com.example.bt_canhan_tuan_7.databinding.DialogAddAppointmentBinding
import com.example.bt_canhan_tuan_7.databinding.DialogFilterBinding
import com.example.bt_canhan_tuan_7.databinding.DialogDeleteConfirmationBinding
import com.example.bt_canhan_tuan_7.ui.adapter.AppointmentAdapter
import com.example.bt_canhan_tuan_7.ui.viewmodel.AppointmentViewModel
import com.example.bt_canhan_tuan_7.ui.viewmodel.AppointmentViewModelFactory
import com.example.bt_canhan_tuan_7.data.entity.Appointment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AppointmentListFragment : Fragment() {

    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Fragment view has been destroyed")

    private val viewModel: AppointmentViewModel by viewModels {
        try {
            val database = AppointmentDatabase.getDatabase(requireContext())
            val repository = AppointmentRepository(database.appointmentDao())
            AppointmentViewModelFactory(repository)
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error creating ViewModel", e)
            throw e
        }
    }

    private lateinit var adapter: AppointmentAdapter
    private var selectedAppointment: Appointment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentAppointmentListBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error in onCreateView", e)
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            setupRecyclerView()
            observeData()
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter { appointment ->
            selectedAppointment = appointment
            showDeleteConfirmationDialog()
        }
        binding.appointmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AppointmentListFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.filteredAppointments.collect { appointments ->
                    adapter.updateAppointments(appointments)
                    updateEmptyState(appointments.isEmpty())
                }
            } catch (e: Exception) {
                Log.e("AppointmentListFragment", "Error observing appointments", e)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.isLoading.collect { isLoading ->
                    binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Log.e("AppointmentListFragment", "Error observing loading state", e)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.appointmentRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun showAddAppointmentDialog() {
        try {
            val dialogBinding = DialogAddAppointmentBinding.inflate(LayoutInflater.from(requireContext()))
            
            var fromDateTime = LocalDateTime.now()
            var toDateTime = LocalDateTime.now().plusHours(1)

            // Setup date/time pickers
            dialogBinding.fromDateEditText.setOnClickListener {
                showDatePicker(fromDateTime) { date ->
                    fromDateTime = fromDateTime.with(date)
                    dialogBinding.fromDateEditText.setText(date.toString())
                }
            }

            dialogBinding.fromTimeEditText.setOnClickListener {
                showTimePicker(fromDateTime) { time ->
                    fromDateTime = fromDateTime.with(time)
                    dialogBinding.fromTimeEditText.setText(String.format("%02d:%02d", time.hour, time.minute))
                }
            }

            dialogBinding.toDateEditText.setOnClickListener {
                showDatePicker(toDateTime) { date ->
                    toDateTime = toDateTime.with(date)
                    dialogBinding.toDateEditText.setText(date.toString())
                }
            }

            dialogBinding.toTimeEditText.setOnClickListener {
                showTimePicker(toDateTime) { time ->
                    toDateTime = toDateTime.with(time)
                    dialogBinding.toTimeEditText.setText(String.format("%02d:%02d", time.hour, time.minute))
                }
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            dialogBinding.addButton.setOnClickListener {
                val name = dialogBinding.appointmentNameEditText.text.toString()
                val description = dialogBinding.descriptionEditText.text.toString()
                val location = dialogBinding.locationEditText.text.toString()
                val personName = dialogBinding.personNameEditText.text.toString()
                val avatarUrl = dialogBinding.avatarUrlEditText.text.toString()

                if (name.isBlank() || description.isBlank() || location.isBlank() || 
                    personName.isBlank() || avatarUrl.isBlank()) {
                    dialogBinding.errorMessageTextView.apply {
                        text = "Vui lòng điền đầy đủ tất cả các trường"
                        visibility = View.VISIBLE
                    }
                    return@setOnClickListener
                }

                if (fromDateTime.isAfter(toDateTime)) {
                    dialogBinding.errorMessageTextView.apply {
                        text = "Thời gian bắt đầu phải trước thời gian kết thúc"
                        visibility = View.VISIBLE
                    }
                    return@setOnClickListener
                }

                val appointment = Appointment(
                    name = name,
                    description = description,
                    location = location,
                    personName = personName,
                    personAvatarUrl = avatarUrl,
                    fromTime = fromDateTime,
                    toTime = toDateTime
                )

                viewModel.addAppointment(appointment)
                dialog.dismiss()
            }

            dialogBinding.cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error showing add dialog", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun showFilterDialog() {
        try {
            val dialogBinding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))

            var fromDateTime = LocalDateTime.now()
            var toDateTime = LocalDateTime.now().plusDays(7)

            dialogBinding.filterFromDateEditText.apply {
                setText(fromDateTime.toLocalDate().toString())
                setOnClickListener {
                    showDatePicker(fromDateTime) { date ->
                        fromDateTime = fromDateTime.with(date)
                        setText(date.toString())
                    }
                }
            }

            dialogBinding.filterFromTimeEditText.apply {
                setText(String.format("%02d:%02d", fromDateTime.hour, fromDateTime.minute))
                setOnClickListener {
                    showTimePicker(fromDateTime) { time ->
                        fromDateTime = fromDateTime.with(time)
                        setText(String.format("%02d:%02d", time.hour, time.minute))
                    }
                }
            }

            dialogBinding.filterToDateEditText.apply {
                setText(toDateTime.toLocalDate().toString())
                setOnClickListener {
                    showDatePicker(toDateTime) { date ->
                        toDateTime = toDateTime.with(date)
                        setText(date.toString())
                    }
                }
            }

            dialogBinding.filterToTimeEditText.apply {
                setText(String.format("%02d:%02d", toDateTime.hour, toDateTime.minute))
                setOnClickListener {
                    showTimePicker(toDateTime) { time ->
                        toDateTime = toDateTime.with(time)
                        setText(String.format("%02d:%02d", time.hour, time.minute))
                    }
                }
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            dialogBinding.filterButton.setOnClickListener {
                if (fromDateTime.isAfter(toDateTime)) {
                    dialogBinding.filterErrorMessageTextView.apply {
                        text = "Thời gian bắt đầu phải trước thời gian kết thúc"
                        visibility = View.VISIBLE
                    }
                    return@setOnClickListener
                }

                viewModel.filterAppointmentsByTimeRange(fromDateTime, toDateTime)
                dialog.dismiss()
            }

            dialogBinding.filterCancelButton.setOnClickListener {
                viewModel.resetFilter()
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error showing filter dialog", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        try {
            selectedAppointment?.let { appointment ->
                val dialogBinding = DialogDeleteConfirmationBinding.inflate(LayoutInflater.from(requireContext()))
                dialogBinding.appointmentNameToDeleteTextView.text = appointment.name

                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.deleteConfirmButton.setOnClickListener {
                    viewModel.deleteAppointment(appointment)
                    dialog.dismiss()
                    selectedAppointment = null
                }

                dialogBinding.deleteCancelButton.setOnClickListener {
                    dialog.dismiss()
                    selectedAppointment = null
                }

                dialog.show()
            }
        } catch (e: Exception) {
            Log.e("AppointmentListFragment", "Error showing delete dialog", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker(currentDateTime: LocalDateTime, onDateSelected: (LocalDate) -> Unit) {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            currentDateTime.year,
            currentDateTime.monthValue - 1,
            currentDateTime.dayOfMonth
        )
        datePicker.show()
    }

    private fun showTimePicker(currentDateTime: LocalDateTime, onTimeSelected: (LocalTime) -> Unit) {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                onTimeSelected(LocalTime.of(hour, minute))
            },
            currentDateTime.hour,
            currentDateTime.minute,
            true
        )
        timePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
