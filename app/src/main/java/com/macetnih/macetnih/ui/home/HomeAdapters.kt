package com.macetnih.macetnih.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.macetnih.macetnih.databinding.ListItemMacetBinding
import com.macetnih.macetnih.domain.model.MacetModel

class HomeAdapters(options: FirestoreRecyclerOptions<MacetModel>) : FirestoreRecyclerAdapter<MacetModel, HomeAdapters.HomeViewHolder>(options) {
    inner class HomeViewHolder(val binding: ListItemMacetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ListItemMacetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    private var setOnItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.setOnItemClickCallback = onItemClickCallback
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int, model: MacetModel) {
            holder.binding.apply {
                this.tvListStreet.text = model.street
                this.tvListStatus.text = model.status
            }

        holder.binding.let {
            it.root.setOnClickListener {
                setOnItemClickCallback?.onItemClicked(model)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MacetModel)
    }
}