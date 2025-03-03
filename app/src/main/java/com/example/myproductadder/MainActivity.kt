package com.example.myproductadder

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myproductadder.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(p0: ColorEnvelope?, p1: Boolean) {
                        p0?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }

                })
                .setNegativeButton("cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()

        }
    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.saveProduct) {
            if(validateProduct())  {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getSizeList(size: String): List<String>? {
        if(size.isEmpty()) {
            return null
        }
        val sizeList = size.split(",").toMutableList()
        return sizeList
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val offerPercent = binding.offerPercentage.text.toString().trim()
        val selectedSizes = getSizeList(binding.edSizes.text.toString().trim())

    }
    private fun validateProduct(): Boolean {
        if(binding.edName.text.toString().trim().isEmpty()){
            return false
        }
        if(binding.edPrice.text.toString().trim().isEmpty()){
            return false
        }
        if(binding.edCategory.text.toString().trim().isEmpty()){
            return false
        }
        if(selectedImages.isEmpty()){
            return false
        }
        return true
    }
}