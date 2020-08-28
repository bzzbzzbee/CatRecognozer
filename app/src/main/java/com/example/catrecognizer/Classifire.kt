package com.example.catrecognizer
import android.graphics.Bitmap

interface Classifier {
    fun recognizeImage(bitmap: Bitmap): Float
}