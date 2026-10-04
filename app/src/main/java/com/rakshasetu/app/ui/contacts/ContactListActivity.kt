package com.rakshasetu.app.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.EmergencyContact
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.databinding.ActivityContactListBinding
import com.rakshasetu.app.ui.adapter.ContactAdapter
import com.rakshasetu.app.util.SmsVerificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactListActivity : AppCompatActivity(), ContactAdapter.OnContactActionListener {

    @Inject lateinit var contactRepository: ContactRepository

    private lateinit var binding: ActivityContactListBinding
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupAddButton()
        observeContacts()
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Drag-to-reorder priority: one drag, one save — no "apply" button.
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.bindingAdapterPosition
                val toPos = target.bindingAdapterPosition
                if (fromPos == RecyclerView.NO_POSITION || toPos == RecyclerView.NO_POSITION) return false
                adapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                // Tactile drag feedback: the lifted row dims, nothing else moves.
                viewHolder?.itemView?.alpha = if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) 0.6f else 1f
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1f
                lifecycleScope.launch {
                    contactRepository.reorderContacts(adapter.currentList)
                }
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAddButton() {
        binding.fabAdd.setOnClickListener {
            if (adapter.currentList.size >= 5) {
                Toast.makeText(this, "Maximum 5 emergency contacts allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    private fun observeContacts() {
        lifecycleScope.launch {
            contactRepository.getAllContacts().collectLatest { contacts ->
                adapter.submitList(contacts)
                // Toggle the whole empty-state block, not just its text, so the
                // icon and heading never linger over a populated list.
                binding.layoutEmpty.visibility = if (contacts.isEmpty()) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }
    }

    override fun onEditClick(contact: EmergencyContact) {
        val intent = Intent(this, AddContactActivity::class.java).apply {
            putExtra(AddContactActivity.EXTRA_CONTACT_ID, contact.id)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(contact: EmergencyContact) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Remove ${contact.name} from emergency contacts?\n\nThey will no longer receive alerts.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    contactRepository.deleteContact(contact)
                    Toast.makeText(this@ContactListActivity, "${contact.name} removed", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onTestAlertClick(contact: EmergencyContact) {
        AlertDialog.Builder(this)
            .setTitle("Send Test Alert to ${contact.name}?")
            .setMessage(
                "This will send a TEST SMS to ${contact.fullPhoneNumber}.\n\n" +
                "The message will be clearly marked as a TEST so they know it's not a real emergency.\n\n" +
                "They'll receive a message like:\n\n" +
                "\"🧪 TEST ALERT from RakshaSetu — This is a test message...\""
            )
            .setPositiveButton("Send Test SMS") { _, _ ->
                val success = SmsVerificationHelper.sendTestSms(this, contact, contact.id.toInt())
                if (success) {
                    Toast.makeText(this, "Test SMS sent to ${contact.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send test SMS", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
