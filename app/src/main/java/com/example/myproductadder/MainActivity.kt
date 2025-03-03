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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  var selectedImages = mutableListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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