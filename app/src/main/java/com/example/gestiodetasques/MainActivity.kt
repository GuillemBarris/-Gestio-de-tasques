package com.example.gestiodetasques

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestiodetasques.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TascaAdapter
    private lateinit var taskGrid: RecyclerView
    private lateinit var taskDatabase: Database
    private var selectedTask: Tasca? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.btcrearTasca.setOnClickListener {
            val intent = Intent(this, CrearTasca::class.java)
            startActivity(intent)
        }

        binding.btBorrarTasca.setOnClickListener {
            selectedTask?.let { task ->
                try {
                    taskDatabase.deleteTask(task)
                } catch (e: SQLiteException) {
                    Log.e("MainActivity", "Error deleting task: ${e.message}")
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnActualizar.setOnClickListener {
            selectedTask?.let { task ->
                if (task!= null) {
                    try {
                        val bundle = Bundle()
                        bundle.putString("id", task.id)
                        bundle.putString("titol", task.titol)
                        bundle.putString("descripcioCurta", task.descripcioCurta)
                        bundle.putString("descripcioLlarga", task.descripcioLlarga)
                        bundle.putString("idImg", task.ID_IMG)
                        bundle.putString("dataCreacio", task.dataCreacio)
                        bundle.putString("dataPrevista", task.dataPrevista)
                        bundle.putString("dataFinal", task.dataFinal)
                        bundle.putString("Estat", task.Estat)
                        bundle.putString("Ordre", task.Ordre.toString())
                        val intent = Intent(this, ActualizarTasca::class.java)
                        intent.putExtra("task_bundle", bundle)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error creating intent: ${e.message}")
                    }
                } else {
                    Log.e("MainActivity", "Selected task is null")
                }
            }

        }

        taskGrid = binding.taskGrid
        taskGrid.layoutManager = GridLayoutManager(this, 1)

        taskDatabase = DatabaseHelper.getInstance(this)

        // Fetch tasks from database
        val tasks = taskDatabase.getAllTasks()

        taskAdapter = TascaAdapter(applicationContext, tasks.toMutableList())
        taskGrid.adapter = taskAdapter

        taskAdapter.setOnItemClickListener(object : TascaAdapter.OnItemClickListener {
            override fun onItemClick(task: Tasca, view: View) {
                if (task!= null) {
                    Log.d("MainActivity", "Selected task: ${task.id}")
                    selectedTask = task
                } else {
                    Log.e("MainActivity", "Task is null")
                }
            }
        })
        val itemTouchHelperCallback = TascaItemTouchHelperCallback(taskAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(taskGrid)
    }
}