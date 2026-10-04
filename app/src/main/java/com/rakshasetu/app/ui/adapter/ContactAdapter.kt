package com.rakshasetu.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.EmergencyContact

class ContactAdapter(
    private val listener: OnContactActionListener
) : ListAdapter<EmergencyContact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    interface OnContactActionListener {
        fun onEditClick(contact: EmergencyContact)
        fun onDeleteClick(contact: EmergencyContact)
        fun onTestAlertClick(contact: EmergencyContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact, position)
    }

    fun moveItem(from: Int, to: Int) {
        val mutableList = currentList.toMutableList()
        val item = mutableList.removeAt(from)
        mutableList.add(to, item)
        // Update priorities
        mutableList.forEachIndexed { index, contact ->
            contact.priority = index
        }
        submitList(mutableList)
        notifyItemMoved(from, to)
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvContactName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvContactPhone)
        private val tvRelation: TextView = itemView.findViewById(R.id.tvContactRelation)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        private val tvVerified: TextView = itemView.findViewById(R.id.tvVerified)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val btnTest: ImageButton = itemView.findViewById(R.id.btnTest)

        fun bind(contact: EmergencyContact, position: Int) {
            tvName.text = contact.name
            tvPhone.text = contact.fullPhoneNumber
            tvRelation.text = contact.displayRelation
            tvPriority.text = (position + 1).toString()
            tvVerified.visibility = if (contact.isVerified) View.VISIBLE else View.GONE

            btnEdit.setOnClickListener { listener.onEditClick(contact) }
            btnDelete.setOnClickListener { listener.onDeleteClick(contact) }
            btnTest.setOnClickListener { listener.onTestAlertClick(contact) }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<EmergencyContact>() {
        override fun areItemsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EmergencyContact, newItem: EmergencyContact) =
            oldItem == newItem
    }
}
