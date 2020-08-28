package com.example.catrecognizer

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

 class ImageClassifier(var tflite : Interpreter)  : Classifier {
  private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
  val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
  byteBuffer.order(ByteOrder.nativeOrder())
  val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
  bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
  var pixel = 0
  for (i in 0 until IMAGE_SIZE) {
   for (j in 0 until IMAGE_SIZE) {
   val `val` = intValues[pixel++]
   byteBuffer.put((`val` shr 16 and 0xFF).toByte())
   byteBuffer.put((`val` shr 8 and 0xFF).toByte())
   byteBuffer.put((`val` and 0xFF).toByte())
   }
  }
 return byteBuffer
 }

  override fun recognizeImage(bitmap: Bitmap): Float {
  val byteBuffer = convertBitmapToByteBuffer(bitmap)
   var result = Array(1) { FloatArray(1) }

   tflite.run(byteBuffer, result)
  return result[0][0]
  }
 }