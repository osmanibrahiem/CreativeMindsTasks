package com.cems.devtask.ui.adapter

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.cems.devtask.model.ReposItem
import com.cems.devtask.ui.adapter.viewHolder.ReposViewHolder

class ReposAdapter(private val listener: ((ReposItem) -> Unit)? = null) :
    RecyclerView.Adapter<ReposViewHolder>(), Filterable {

    var items = ArrayList<ReposItem>()
        set(value) {
            val size = field.size
            field.addAll(value)
            val sizeNew = field.size
            notifyItemRangeInserted(size, sizeNew)
        }
    var itemsFiltered = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        return ReposViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        val repoItem = itemsFiltered[position]
        holder.bind(repoItem, listener)
    }

    override fun getItemCount() = itemsFiltered.size

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint.isNullOrEmpty())
                itemsFiltered = items
            else {
                itemsFiltered = ArrayList()
                items.forEach {
                    if (!it.name.isNullOrEmpty() && it.name!!.contains(constraint, true))
                        itemsFiltered.add(it)
                }
            }
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}
