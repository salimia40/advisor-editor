package ir.puyaars.advisor.blog.editor.utils

import androidx.lifecycle.MutableLiveData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class UploadResponse(val name: String?,val success: Boolean)

interface UploadAPI {
    @Multipart
    @POST("/api/upload")
    fun uploadImage(@Part file : MultipartBody.Part): Call<UploadResponse>
}


class Uploader {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://advisor.liara.run")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val uploadAPI = retrofit.create(UploadAPI::class.java)

    fun uploadFile(
        filePath: String
    ): UploadReturn {
        val uploadReturn = UploadReturn()
        executor.execute {
            val file = File(filePath)
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
            val part = MultipartBody.Part.createFormData("file", file.name, fileReqBody)
            val res =  uploadAPI.uploadImage(part).execute()
            if ( res.isSuccessful && res.body()?.success == true) {
                    uploadReturn.result.postValue("https://advisor.liara.run/api/files/${res.body()?.name}")
            } else{
                uploadReturn.error.postValue("upload failed")
            }
        }
        return uploadReturn
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

class UploadReturn {
    val result: MutableLiveData<String> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
}