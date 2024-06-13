package com.example.gestiodetasques

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min


class TascaAdapter(private val context: Context, private val tasks: MutableList<Tasca>) : RecyclerView.Adapter<TascaAdapter.TaskViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(task: Tasca, view: View)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.task_title)
        val taskDescriptionTextView: TextView = itemView.findViewById(R.id.task_description)
        val taskImageView: ImageView = itemView.findViewById(R.id.task_image)
        val taskDueDateTextView: TextView = itemView.findViewById(R.id.task_due_date)
        val expandedContent: LinearLayout = itemView.findViewById(R.id.expanded_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tasca_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.titol
        holder.taskDescriptionTextView.text = task.descripcioCurta
        holder.taskDueDateTextView.text = task.dataPrevista

        val imageUri =
            Uri.parse("/storage/self/primary/Android/data/com.example.gestiodetasques/files/Pictures/${task.ID_IMG}.jpg")
        Log.d("TascaAdapter", "Image Uri: $imageUri")

        if (task.ID_IMG != "") {
            holder.taskImageView.setImageURI(imageUri)
            Log.d("TascaAdapter", "Image set to ImageView")
        } else {
            holder.taskImageView.setImageResource(R.drawable.default_image)
            Log.d("TascaAdapter", "Default image set")
        }

        holder.expandedContent.findViewById<TextView>(R.id.task_long_description).text =
            task.descripcioLlarga
        holder.expandedContent.findViewById<TextView>(R.id.task_creation_date).text =
            task.dataCreacio
        holder.expandedContent.findViewById<TextView>(R.id.task_final_date).text = task.dataFinal
        holder.expandedContent.findViewById<TextView>(R.id.task_status).text = task.Estat

        holder.itemView.setOnClickListener {
            Log.d("TascaAdapter", "Item clicked")
            if (holder.expandedContent.visibility == View.VISIBLE) {
                holder.expandedContent.visibility = View.GONE
            } else {
                holder.expandedContent.visibility = View.VISIBLE
            }
            listener?.onItemClick(task, holder.itemView)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun removeItem(task: Tasca) {
        val index = tasks.indexOf(task)
        if (index != -1) {
            tasks.removeAt(index)
            notifyDataSetChanged()
        }
    }


    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        val task = tasks[fromPosition]
        tasks.removeAt(fromPosition)
        tasks.add(toPosition, task)
        notifyItemMoved(fromPosition, toPosition)

        for (i in tasks.indices) {
            tasks[i].Ordre = i
            DatabaseHelper.getInstance(context)
                .updateTask(tasks[i]) //
        }
    }
}