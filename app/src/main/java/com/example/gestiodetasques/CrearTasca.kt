package com.example.gestiodetasques

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiodetasques.databinding.CrearTascaBinding
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID

class CrearTasca : AppCompatActivity() {


    private lateinit var binding: CrearTascaBinding

    companion object {
        const val SELECT_IMAGE_REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CrearTascaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickDateTime.setOnClickListener {
            val dataPickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                    binding.DataSeleccionada.text = selectedDate

                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            dataPickerDialog.show()

        }
        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnSelectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        }
        binding.btnSaveAll.setOnClickListener {
            val idUid = UUID.randomUUID().toString()
            val imageUid = UUID.randomUUID().toString()
            val currentDate = LocalDate.now().toString()
            val database = Database(this)
            val order = database.getNextOrderIndex()
            val tasca = Tasca(
                idUid,
                binding.titolinput.text.toString(),
                binding.descripcioCurtaInput.text.toString(),
                binding.descripcioLlargaInput.text.toString(),
                imageUid,
                currentDate,
                binding.DataSeleccionada.text.toString(),
                dataFinal = "",
                Estat = "Actiu",
                order
            )
            database.insertTask(tasca)


            database.insertTask(tasca)

            // crear el bitmap
             val bitmap = (binding.ImageView.drawable as BitmapDrawable).bitmap

            // guardar la imatge
            val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val out = File(path, "$imageUid.jpg")
            try{
                val fos = FileOutputStream(out)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: Exception){
                e.printStackTrace()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
