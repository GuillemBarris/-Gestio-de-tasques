package com.example.gestiodetasques

data class Tasca(
    val id: String,
    val titol: String,
    val descripcioCurta: String,
    val descripcioLlarga: String,
    var ID_IMG: String,
    val dataCreacio: String,
    val dataPrevista: String,
    val dataFinal: String,
    val Estat: String,
    var Ordre: Int,

    )