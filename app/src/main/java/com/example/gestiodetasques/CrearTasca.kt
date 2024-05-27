package com.example.gestiodetasques

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiodetasques.databinding.CrearTascaBinding
import java.util.Calendar

class CrearTasca : AppCompatActivity(){
    private lateinit var binding: CrearTascaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrearTascaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickDateTime.setOnClickListener {
            val dataPickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                    binding.DataSeleccionada.text = selectedDate

                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            dataPickerDialog.show()
        }


    }
}