package com.deyryl.task1

/**
 * Задача 1: Приложение для получения контактов на телефоне
 */

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class Contact(
    val id: Long,
    val name: String
)

class MainActivity : ComponentActivity() {
    private val debugTag = "Contacts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            0
        )

        if (hasReadContactsPermission()) {
            val contacts = getContacts()
            Log.d(debugTag, contacts.toString())
            Log.d(debugTag, contacts.size.toString())
        } else {
            Log.d(debugTag, "Permission denied")
        }
    }

    private fun hasReadContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        ).use { cursor ->
            if (cursor == null) return@use

            val idColumn = cursor.getColumnIndexOrThrow(
                ContactsContract.Contacts._ID
            )

            val nameColumn = cursor.getColumnIndexOrThrow(
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: ""
                contacts.add(Contact(id, name))
            }
        }

        return contacts
    }
}