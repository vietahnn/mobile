package com.example.buoi9_cn.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buoi9_cn.R
import com.example.buoi9_cn.data.model.Lich
import com.example.buoi9_cn.databinding.LichItemBinding
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class LichAdapter(
    private var lichList: List<Lich>,
    private val onItemClick: (Lich) -> Unit,
    private val onItemLongClick: ((Lich) -> Unit)? = null
) : RecyclerView.Adapter<LichAdapter.LichViewHolder>() {

    class LichViewHolder(val binding: LichItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LichViewHolder {
        val binding = LichItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LichViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LichViewHolder, position: Int) {
        val lich = lichList[position]

        // Set text data
        holder.binding.tvHoVaTen.text = lich.hoVaTen
        holder.binding.tvNoiDung.text = lich.noiDung

        // Format date time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.binding.tvNgayGio.text = dateFormat.format(Date(lich.ngayGio))

        // Load image from URL in background thread
        if (lich.linkAnh.isNotEmpty()) {
            holder.binding.imgAnh.setImageResource(R.drawable.ic_launcher_foreground) // Default image
            thread {
                try {
                    val url = URL(lich.linkAnh)
                    val input: InputStream = url.openStream()
                    val drawable = Drawable.createFromStream(input, "src")
                    holder.binding.root.post {
                        holder.binding.imgAnh.setImageDrawable(drawable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Keep default image on error
                }
            }
        } else {
            holder.binding.imgAnh.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(lich)
        }

        holder.binding.root.setOnLongClickListener {
            onItemLongClick?.invoke(lich)
            true
        }
    }

    override fun getItemCount(): Int = lichList.size

    fun updateList(newList: List<Lich>) {
        lichList = newList
        notifyDataSetChanged()
    }
}

