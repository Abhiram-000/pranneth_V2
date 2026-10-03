package com.rakshasetu.app.ui.contacts

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.EmergencyContact
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.databinding.ActivityAddContactBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddContactActivity : AppCompatActivity() {

    @Inject lateinit var contactRepository: ContactRepository

    private lateinit var binding: ActivityAddContactBinding
    private var editingContactId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingContactId = intent.getLongExtra(EXTRA_CONTACT_ID, -1)

        setupToolbar()
        setupRelationDropdown()
        setupSaveButton()

        if (editingContactId != -1L) {
            loadContact()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (editingContactId == -1L) "Add Contact" else "Edit Contact"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRelationDropdown() {
        val relations = EmergencyContact.RELATIONS
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, relations)
        binding.dropdownRelation.setAdapter(adapter)
        binding.dropdownRelation.setOnItemClickListener { _, _, position, _ ->
            val selected = relations[position]
            binding.layoutCustomRelation.visibility = if (selected == "Other") {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val countryCode = binding.etCountryCode.text.toString().trim()
            val relation = binding.dropdownRelation.text.toString()
            val customRelation = binding.etCustomRelation.text.toString().trim()

            val validation = com.rakshasetu.app.domain.contacts.ContactValidator.validate(
                name, phone, countryCode, relation, customRelation
            )
            if (!validation.isValid) {
                when {
                    name.isBlank() -> binding.etName.error = validation.error
                    relation == "Other" && customRelation.isBlank() -> binding.etCustomRelation.error = validation.error
                    countryCode.isBlank() -> binding.etCountryCode.error = validation.error
                    else -> binding.etPhone.error = validation.error
                }
                return@setOnClickListener
            }

            val contact = EmergencyContact(
                id = if (editingContactId != -1L) editingContactId else 0,
                name = name,
                phoneNumber = phone,
                countryCode = countryCode,
                relation = relation,
                customRelation = if (relation == "Other") customRelation else null,
                priority = if (editingContactId != -1L) 0 else Int.MAX_VALUE
            )

            lifecycleScope.launch {
                if (editingContactId != -1L) {
                    contactRepository.updateContact(contact)
                    Toast.makeText(this@AddContactActivity, "Contact updated", Toast.LENGTH_SHORT).show()
                } else {
                    contactRepository.addContact(contact)
                    Toast.makeText(this@AddContactActivity, "Contact added", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun validateInputs(
        name: String,
        phone: String,
        countryCode: String,
        relation: String
    ): Boolean {
        if (name.isBlank()) {
            binding.etName.error = "Name is required"
            return false
        }

        if (phone.isBlank()) {
            binding.etPhone.error = "Phone number is required"
            return false
        }

        // Validate phone number format (basic Indian number validation)
        val cleanPhone = phone.replace("\\s".toRegex(), "")
        if (!cleanPhone.matches(Regex("^[6-9]\\d{9}$"))) {
            binding.etPhone.error = "Enter a valid 10-digit Indian mobile number"
            return false
        }

        if (countryCode.isBlank()) {
            binding.etCountryCode.error = "Country code required"
            return false
        }

        if (relation.isBlank()) {
            Toast.makeText(this, "Select a relation", Toast.LENGTH_SHORT).show()
            return false
        }

        if (relation == "Other" && binding.etCustomRelation.text.isNullOrBlank()) {
            binding.etCustomRelation.error = "Enter custom relation"
            return false
        }

        return true
    }

    private fun loadContact() {
        lifecycleScope.launch {
            contactRepository.getContactById(editingContactId)?.let { contact ->
                binding.etName.setText(contact.name)
                binding.etPhone.setText(contact.phoneNumber)
                binding.etCountryCode.setText(contact.countryCode)
                binding.dropdownRelation.setText(contact.relation, false)
                if (contact.relation == "Other") {
                    binding.etCustomRelation.setText(contact.customRelation ?: "")
                    binding.layoutCustomRelation.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    companion object {
        const val EXTRA_CONTACT_ID = "contact_id"
    }
}
