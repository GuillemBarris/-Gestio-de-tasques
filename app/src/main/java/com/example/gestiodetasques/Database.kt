package com.example.gestiodetasques

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import android.util.Printer
import java.sql.SQLException
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock


class Database(context: Context) : SQLiteOpenHelper(context, Database_Name, null, Database_Version) {
    // ...

    companion object {



        private const val Database_Version = 1
        private const val Database_Name = "GestioTasques.sqlite"
        private const val Table_Name = "tasques"
        private const val Column_Id = "Id"
        private const val Column_Title = "Titol"
        private const val Column_Short_Description = "Descripcio_Curta"
        private const val Column_Long_Description = "Descripcio_Llarga"
        private const val Column_Id_IMG = "ID_Imatge"
        private const val Column_Date_Creation = "Data_Creacio"
        private const val Column_Date_Expected = "Data_Prevista"
        private const val Column_Date_Final = "Data_Finalizada"
        private const val Column_State = "Estat"
        private const val Column_Order = "Ordre"

    }


    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE IF NOT EXISTS " + Table_Name + " ("
                + Column_Id + " TEXT PRIMARY KEY, "
                + Column_Title + " TEXT, " +
                Column_Short_Description + " TEXT, " +
                Column_Long_Description + " TEXT, " +
                Column_Id_IMG + " TEXT, " +
                Column_Date_Creation + " TEXT, " +
                Column_Date_Expected + " TEXT, " +
                Column_Date_Final + " TEXT, " +
                Column_State + " TEXT, " +
                Column_Order + " INTEGER DEFAULT 0);")
        db.execSQL(createTable)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $Table_Name")
        onCreate(db)

    }

    fun insertTask(tasca: Tasca) {
        val values = ContentValues()
        values.put(Column_Id, tasca.id)
        values.put(Column_Title, tasca.titol)
        values.put(Column_Short_Description, tasca.descripcioCurta)
        values.put(Column_Long_Description, tasca.descripcioLlarga)
        values.put(Column_Id_IMG, tasca.ID_IMG)
        values.put(Column_Date_Creation, tasca.dataCreacio)
        values.put(Column_Date_Expected, tasca.dataPrevista)
        values.put(Column_State, tasca.Estat)
        values.put(Column_Order, getNextOrderIndex())

        writableDatabase.use { db ->
            db.insert(Table_Name, null, values)
        }
    }

    public fun getNextOrderIndex(): Int {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT COALESCE(MAX($Column_Order), 0) + 1 FROM $Table_Name", null)
        cursor.moveToFirst()
        val nextOrderIndex = cursor.getInt(0)
        cursor.close()
        return nextOrderIndex
    }
    fun getAllTasks(): List<Tasca> {
        val tasks = mutableListOf<Tasca>()
        val db = readableDatabase
        try {
            val cursor = db.rawQuery("SELECT * from $Table_Name Order by $Column_Order ASC;", null)
            try {
                if (cursor.moveToFirst()) {
                    do {
                        val columnIndexId = cursor.getColumnIndex(Column_Id)
                        val columnIndexTitle = cursor.getColumnIndex(Column_Title)
                        val columnIndexShortDescription =
                            cursor.getColumnIndex(Column_Short_Description)
                        val columnIndexLongDescription =
                            cursor.getColumnIndex(Column_Long_Description)
                        val columnIndexIdImg = cursor.getColumnIndex(Column_Id_IMG)
                        val columnIndexDateCreation = cursor.getColumnIndex(Column_Date_Creation)
                        val columnIndexDateExpected = cursor.getColumnIndex(Column_Date_Expected)
                        val columnIndexDateFinal = cursor.getColumnIndex(Column_Date_Final)
                        val columnIndexState = cursor.getColumnIndex(Column_State)
                        val columnIndexOrder = cursor.getColumnIndex(Column_Order)
                        if (columnIndexId == -1 || columnIndexTitle == -1 || columnIndexShortDescription == -1 || columnIndexLongDescription == -1 || columnIndexIdImg == -1 || columnIndexDateCreation == -1 || columnIndexDateExpected == -1 || columnIndexDateFinal == -1 || columnIndexState == -1) {
                            throw RuntimeException("Invalid column index")
                        }

                        val task = Tasca(
                            cursor.getString(columnIndexId) ?: "",
                            cursor.getString(columnIndexTitle) ?: "",
                            cursor.getString(columnIndexShortDescription) ?: "",
                            cursor.getString(columnIndexLongDescription) ?: "",
                            cursor.getString(columnIndexIdImg) ?: "",
                            cursor.getString(columnIndexDateCreation) ?: "",
                            cursor.getString(columnIndexDateExpected) ?: "",
                            cursor.getString(columnIndexDateFinal) ?: "",
                            cursor.getString(columnIndexState) ?: "",
                            cursor.getInt(columnIndexOrder) ?: 0
                        )
                        tasks.add(task)
                    } while (cursor.moveToNext())
                }
            } finally {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("Database", "Error getting all tasks", e)
            return emptyList()
        } finally {
            db.close()
        }
        return tasks
    }

    fun deleteTask(selectedTask: Tasca) {
        try {
            val db = writableDatabase
            db.delete(Table_Name, "$Column_Id =?", arrayOf(selectedTask.id))
            db.close()
        } catch (e: Exception) {
            Log.e("Database", "Error deleting task", e)
        }

        }


    fun updateTask(tasca: Tasca) {
        val values = ContentValues()
        values.put(Column_Id, tasca.id)
        values.put(Column_Title, tasca.titol)
        values.put(Column_Short_Description, tasca.descripcioCurta)
        values.put(Column_Long_Description, tasca.descripcioLlarga)
        values.put(Column_Id_IMG, tasca.ID_IMG)
        values.put(Column_Date_Creation, tasca.dataCreacio)
        values.put(Column_Date_Expected, tasca.dataPrevista)
        values.put(Column_Date_Final, tasca.dataFinal)
        values.put(Column_State, tasca.Estat)
        values.put(Column_Order, tasca.Ordre)

        writableDatabase.use { db ->
            val rowsAffected = db.update(Table_Name, values, "$Column_Id =?", arrayOf(tasca.id))
            db.close()
            if (rowsAffected == 0) {
                Log.e("DatabaseError", "Error updating task: no rows affected")
            } else {

            }
        }
    }




}