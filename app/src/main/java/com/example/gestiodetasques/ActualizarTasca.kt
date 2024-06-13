package com.example.gestiodetasques

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiodetasques.databinding.ActualizarTascaBinding
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID


class ActualizarTasca : AppCompatActivity() {

    private lateinit var binding: ActualizarTascaBinding
    private lateinit var task: Tasca

    companion object {
        private const val REQUEST_IMAGE_SELECT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActualizarTascaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle = intent.getBundleExtra("task_bundle")
        task = if (bundle != null) {
            Tasca(
                bundle.getString("id", "") ?: "",
                bundle.getString("titol", "") ?: "",
                bundle.getString("descripcioCurta", "") ?: "",
                bundle.getString("descripcioLlarga", "") ?: "",
                bundle.getString("idImg", "") ?: "",
                bundle.getString("dataCreacio", "") ?: "",
                bundle.getString("dataPrevista", "") ?: "",
                bundle.getString("dataFinal", "") ?: "",
                bundle.getString("Estat", "") ?: "",
                bundle.getInt("Order", 0) ?: 0
            )
        } else {
            // Gestiona el cas quan el paquet és nul
            Tasca("", "", "", "", "", "", "", "", "", 0)
        }

        binding.titolinput.setText(task.titol)
        binding.descripcioCurtaInput.setText(task.descripcioCurta)
        binding.descripcioLlargaInput.setText(task.descripcioLlarga)
        val imageUri =
            Uri.parse("/storage/self/primary/Android/data/com.example.gestiodetasques/files/Pictures/${task.ID_IMG}.jpg")
        Log.d("TascaAdapter", "Image Uri: $imageUri")

        if (task.ID_IMG != "") {
            binding.ImageView.setImageURI(imageUri)
            Log.d("TascaAdapter", "Image set to ImageView")
        } else {

            Log.d("TascaAdapter", "Default image set")
        }



        binding.data.setText(task.dataPrevista)

// Estableix l'estat de la casella de selecció en funció del valor Estat
        binding.cbFinalizada.isChecked = task.Estat == "Finalizada"

        binding.btnSelectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, CrearTasca.SELECT_IMAGE_REQUEST_CODE)
            val imageUid = UUID.randomUUID().toString()

        }

        binding.cbFinalizada.setOnCheckedChangeListener { _, isChecked ->
            val currentDate = LocalDate.now().toString()
            task = task.copy(
                Estat = if (isChecked) "Finalizada" else "Activa",
                dataFinal = if (isChecked) currentDate else ""
            )
        }

        binding.btnPickDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    task = task.copy(dataPrevista = "$day/${month + 1}/$year")
                    binding.data.text = task.dataPrevista
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.updateButton.setOnClickListener {
            val id = task.id;
            val titol = binding.titolinput.text.toString()
            val descripcioCurta = binding.descripcioCurtaInput.text.toString().trim()
            val descripcioLlarga = task.descripcioLlarga
            val idImg = task.ID_IMG
            val dataCreacio = task.dataCreacio
            val dataPrevista = task.dataPrevista
            val dataFinal = task.dataFinal
            val estat = task.Estat
            val Order = task.Ordre
            val updatedTask = Tasca(
                id = id,
                titol = titol,
                descripcioCurta = descripcioCurta,
                descripcioLlarga = descripcioLlarga,
                ID_IMG = idImg,
                dataCreacio = dataCreacio,
                dataPrevista = dataPrevista,
                dataFinal = dataFinal,
                Estat = estat,
                Order
            )

            val database = DatabaseHelper.getInstance(this)
            database.updateTask(updatedTask)
            // crear el bitmap
            val bitmap = (binding.ImageView.drawable as BitmapDrawable).bitmap

            // guardar la imatge
            val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val out = File(path, "${task.ID_IMG}.jpg")
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

        if (requestCode == CrearTasca.SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                binding.ImageView.setImageURI(it)
                val selectedImagePath = getRealPathFromURI(it)
                task.ID_IMG = selectedImagePath
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()
        val imageUid = UUID.randomUUID().toString()
        return imageUid
    }
}