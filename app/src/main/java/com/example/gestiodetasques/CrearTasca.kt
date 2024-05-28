

package com.example.gestiodetasques

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiodetasques.databinding.CrearTascaBinding
import java.util.Calendar

class CrearTasca : AppCompatActivity() {
    private lateinit var binding: CrearTascaBinding

    companion object {
        private const val SELECT_IMAGE_REQUEST_CODE = 100
    }
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
        binding.btnSelectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            binding.ImageView.setImageURI(selectedImageUri)
        }
    }

}
