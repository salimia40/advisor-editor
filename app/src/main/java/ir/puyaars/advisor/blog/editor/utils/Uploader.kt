package ir.puyaars.advisor.blog.editor.utils

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

data class UploadResponse(val name: String?, val success: Boolean)

interface UploadAPI {
    @Multipart
    @POST("/api/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<UploadResponse>
}


class Uploader {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://advisor.liara.run")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val uploadAPI = retrofit.create(UploadAPI::class.java)

    fun uploadFile(
        filePath: String,
        listener: UploadResListener
    ) {
        val file = File(filePath)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("file", file.name, fileReqBody)
        uploadAPI.uploadImage(part).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                listener.onUpFail()
            }

            override fun onResponse(call: Call<UploadResponse>, res: Response<UploadResponse>) {
                Log.i("blog app ", res.errorBody().toString())
                Log.i("blog app ", res.toString())
                Log.i("blog app ", res.body().toString())
                if (res.isSuccessful && res.body()?.success == true) {
                    listener.onUpSuccess("https://advisor.liara.run/api/files/${res.body()?.name}")
                } else {
                    listener.onUpFail()
                }
            }

        })

    }

    companion object {
        @Volatile
        private var INSTANCE: Uploader? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Uploader().also { INSTANCE = it }
            }
    }
}

interface UploadResListener {
    fun onUpSuccess(url: String)
    fun onUpFail()
}