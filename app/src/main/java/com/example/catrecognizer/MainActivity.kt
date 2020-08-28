package com.example.catrecognizer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.round


const val MODEL_FILE_PATH = "model.tflite"

const val IMAGE_SIZE = 150

const val REQUEST_TAKE_PICTURE = 1


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val camButton = findViewById<Button>(R.id.camButton)

        camButton.setOnClickListener {
            val takePic=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePic, REQUEST_TAKE_PICTURE)
        }
    }

    fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==  REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            val bmp= data?.extras?.get("data") as Bitmap
            var result: Float
            resultImage.setImageBitmap(bmp)
            resultsTextView.text = "Thinking..."
            try {
                val modelBuffer = loadModelFile(assets, MODEL_FILE_PATH)
                val tflite = Interpreter(modelBuffer)
                result = (ImageClassifier(tflite).recognizeImage(bmp))
                tflite.close()
                result = round(result * 100)
                resultsTextView.text = "$result %"
            } catch (e: Exception) {
                throw RuntimeException("Error initializing classifiers!", e)
            }
        }
    }
}

