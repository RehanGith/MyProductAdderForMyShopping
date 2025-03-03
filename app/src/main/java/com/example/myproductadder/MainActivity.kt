package com.example.myproductadder

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myproductadder.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()
    private var productStorage = Firebase.storage.reference
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
        val activityResult = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {result ->
            if(intent?.clipData  != null) {
                val count = intent?.clipData?.itemCount ?: 0
                (0 until count).forEach {
                    val imageUri = intent?.clipData?.getItemAt(it)?.uri
                    imageUri?.let {
                        selectedImages.add(it)
                    }
                }
            } else {
                val imageUri = intent?.data
                imageUri?.let {
                    selectedImages.add(it)
                }
            }
            updateImages()
        }
        //launching an intent to get images
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            activityResult.launch(intent.toString())
        }
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
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
        val imagesByteArray = getImagesByteArray()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                async {
                    imagesByteArray.forEach {
                    val id = UUID.randomUUID().toString()
                    launch {
                        val imageStorage = productStorage.child("products/images/$id")
                        val result = imageStorage.putBytes(it).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        images.add(downloadUrl)
                    }
                    }
                }.await()

            } catch (e: Exception) {
                e.printStackTrace()
                hideProgressBar()
            }
            val product = Product(
                id = UUID.randomUUID().toString(),
                name = name,
                category = category,
                price = price.toFloat(),
                offerPercentage = if(offerPercent.isEmpty()) null else offerPercent.toFloat().toString(),
                description = if (description.isEmpty()) null else description,
                colors = if (selectedColors.isEmpty()) null else selectedColors,
                sizes = selectedSizes,
                images = images
            )
        }
    }

    private fun hideProgressBar() {
        TODO("Not yet implemented")
    }

    private fun getImagesByteArray(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val bytes = contentResolver.openInputStream(it)?.readBytes()
            bytes?.let {
                imagesByteArray.add(it)
            }
        }
        return imagesByteArray
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