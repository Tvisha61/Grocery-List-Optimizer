package com.example.groceryoptimizer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.groceryoptimizer.databinding.ActivityAddItemBinding
import com.example.groceryoptimizer.db.InMemoryDb
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class AddItemActivity : AppCompatActivity() {
    private lateinit var b: ActivityAddItemBinding

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val inputImage = InputImage.fromBitmap(bitmap, 0) // 0 rotation
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        val item = InMemoryDb.getAll().find { it.barcode == rawValue }
                        if (item != null) {
                            b.etName.setText(item.name)
                            b.etPrice.setText(item.price.toString())
                            b.etBarcode.setText(item.barcode)
                            Toast.makeText(this, "Item found!", Toast.LENGTH_SHORT).show()
                        } else {
                            b.etBarcode.setText(rawValue)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to scan barcode.", Toast.LENGTH_SHORT).show()
                }

            Toast.makeText(this, "Image captured. ML Kit scanning done.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
            showManualBarcodeDialog()
        } else {
            showManualBarcodeDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnScan.setOnClickListener { maybeOpenCameraOrManual() }
        //b.btnScanItem.setOnClickListener { maybeOpenCameraOrManual() } // ✅ added here

        b.btnSave.setOnClickListener {
            val name = b.etName.text?.toString()?.trim().orEmpty()
            val qty = b.etQty.text?.toString()?.toIntOrNull() ?: 0
            val price = b.etPrice.text?.toString()?.toDoubleOrNull() ?: 0.0
            val barcode = b.etBarcode.text?.toString()?.trim().takeIf { !it.isNullOrEmpty() }

            if (name.isEmpty() || qty <= 0 || price <= 0.0) {
                Toast.makeText(this, "Enter valid name, quantity and price.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Insert into in-memory DB
            InMemoryDb.insertItem(name, qty, price, barcode)

            Toast.makeText(this, "Item saved successfully.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun maybeOpenCameraOrManual() {
        val has = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (has) {
            cameraLauncher.launch(null)
            showManualBarcodeDialog()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showManualBarcodeDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Enter barcode"
        android.app.AlertDialog.Builder(this)
            .setTitle("Scan (Manual)")
            .setView(input)
            .setPositiveButton("OK") { d, _ ->
                b.etBarcode.setText(input.text?.toString()?.trim())
                d.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
