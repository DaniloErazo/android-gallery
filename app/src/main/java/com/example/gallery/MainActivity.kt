package com.example.gallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.gallery.databinding.ActivityMainBinding
import com.example.gallery.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ::onGalleryResult
    )

    val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        vm.showUser()

        vm.userLD.observe(this){
            Toast.makeText(this, it.photoUrl, Toast.LENGTH_LONG).show()
            binding.nameTxt.text = it.name
            Glide.with(this).load(it.photoUrl).into(binding.profileImg)
        }

        binding.profileImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)

            intent.type = "image/*"
            launcher.launch(intent)
        }
    }

    fun onGalleryResult(result:ActivityResult){
        val uri = result.data?.data
        Glide.with(this).load(uri).into(binding.profileImg)
        binding.testo.text = "está lindo compa"
        uri?.let{
            vm.uploadImage(it)
        }
        //vm.uploadImage(uri!!)
    }
}