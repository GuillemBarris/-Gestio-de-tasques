package com.example.gestiodetasques

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiodetasques.databinding.CrearTascaBinding

class CrearTasca : AppCompatActivity(){
    private lateinit var binding: CrearTascaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrearTascaBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}