package com.example.gestiodetasques

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues



class Database(context: Context) : SQLiteOpenHelper(context, Database_Name, null, Database_Version){
    companion object {
        private const val Database_Version = 1
        private const val Database_Name= "GestioTasques.sqlite"
        private const val Table_Name = "tasques"
        private const val Column_Title = "Titol"
        private const val Column_Short_Description = "Descripcio Curta"
        private const val Column_Long_Description = "Descripcio Llarga"
        private const val Columm_Date_Expected = "Data Prevista"
    }

    val d = getDatabasePath(context)
    fun getDatabasePath(context: Context): String {
        val dbFile = context.getDatabasePath(Database_Name)
        val parentDir = dbFile.parentFile
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        return dbFile.absolutePath
    }
    override fun onCreate(db: SQLiteDatabase) {
        val Create_Table = ("CREATE TABLE " +
                Table_Name + "(" +
                Column_Title + " TEXT," +
                Column_Short_Description + " TEXT," +
                Column_Long_Description + " TEXT," +
                Columm_Date_Expected + " TEXT)"
        )
        db.execSQL(Create_Table)

        d
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $Table_Name")
        onCreate(db)

    }

    fun insertTask(tasca: Tasca){
        val values = ContentValues()
        values.put(Column_Title, tasca.titol)
        values.put(Column_Short_Description, tasca.descripcioCurta)
        values.put(Column_Long_Description, tasca.descripcioLlarga)
        values.put(Columm_Date_Expected, tasca.dataPrevista)

        writableDatabase.use { db ->
            db.insert(Table_Name, null, values)
        }
    }
}