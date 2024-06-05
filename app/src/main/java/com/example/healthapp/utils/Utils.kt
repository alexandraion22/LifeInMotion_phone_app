package com.example.healthapp.utils

fun calculateBMI(weight: Double, height: Double): Double {
    return weight / ((height/100) * (height/100))
}