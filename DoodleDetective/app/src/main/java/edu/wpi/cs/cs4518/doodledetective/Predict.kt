package edu.wpi.cs.cs4518.doodledetective

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

// This is the data type of the each element in the predictions list
data class  Prediction (
    val predicted_label: String,
    val score: Float
)

// This data type is used to parse the response from JSON to list of predictions
data class PredictionsList(
    val predictions: List<Prediction>
)

private const val TAG = "Predict"

internal class Predict() {

    // Called from ImageClassificationHelper to classify a bitmap image to 5 predictions
    suspend fun classify(bitmap: Bitmap) : PredictionsList{
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "classifying called")
            val byteArray = buildByteArray(bitmap)
            val response = sendPostRequest(byteArray)
            val predictions = parseResponse(response)
            predictions
        }
    }

    //Build the bitmap into a byte stream to be processed
    fun buildByteArray(bitmap: Bitmap) : ByteArray{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return byteArray
    }

    /**
     * Send the byte stream to the server
     *     It converted the array into a MultipartBody data type, so it can be sent as a png file
     *     through the request body
     *     Connect to the server at my public IP address
     *     The response can be parse into a Java object
     */
    fun sendPostRequest(byteArray: ByteArray) : String{
        val client = OkHttpClient()

        val mediaType = "image/png".toMediaType()
        val requestBody = byteArray.toRequestBody(mediaType)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "image.png", requestBody)
            .build()

        //Replace IP with your public IP address of the server
        val request = Request.Builder()
            .url("http://127.0.0.1:5050/predict")
            .post(multipartBody)
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    /**
     * Parse the json response from the server to a list of predictions in Java objects
     * Gson is a JSON parsing library from Google
     */
    fun parseResponse(response: String): PredictionsList {
        val gson = Gson()

        val predictions = gson.fromJson(response, PredictionsList::class.java)

        return predictions
    }
}