package com.example.lab4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab4.databinding.ItemHomeBinding

class HomeAdapter(private val items: List<HomeItem>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class HomeViewHolder(private val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem) {
            binding.itemTitle.text = item.title
            binding.itemSubtitle.text = item.subtitle

            if (item.time != null) {
                binding.itemTime.text = item.time
                binding.itemTime.visibility = View.VISIBLE
            } else {
                binding.itemTime.visibility = View.GONE
            }

            if (item.imageUrl != null) {
                binding.itemImage.visibility = View.VISIBLE
                Glide.with(binding.itemImage.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.itemImage)
            } else {
                binding.itemImage.visibility = View.GONE
            }
        }
    }
}
