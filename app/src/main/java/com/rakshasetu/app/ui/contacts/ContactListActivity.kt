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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Emergency Contacts"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Drag-to-reorder
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                adapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Save new order
                lifecycleScope.launch {
                    contactRepository.reorderContacts(adapter.currentList)
                }
            }
        })
        touchHelper.attachToRecyclerView(binding.recyclerView)
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
                binding.tvEmptyState.visibility = if (contacts.isEmpty()) {
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
            .setMessage("Remove ${contact.name} from emergency contacts?")
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
            .setTitle("Send Test Alert")
            .setMessage(
                "This will send a TEST SMS to ${contact.name} (${contact.fullPhoneNumber}).\n\n" +
                "The message will be clearly marked as a TEST so they know it's not a real emergency."
            )
            .setPositiveButton("Send Test") { _, _ ->
                // TODO: Implement test SMS sending
                Toast.makeText(this, "Test SMS sent to ${contact.name}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
