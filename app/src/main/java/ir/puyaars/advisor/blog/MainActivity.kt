package ir.puyaars.advisor.blog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H3
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader
import com.google.gson.Gson
import ir.puyaars.advisor.blog.editor.EditorControlBar


class MainActivity : AppCompatActivity(), EditorControlBar.EditorControlListener {
    private val REQUEST_IMAGE_SELECTOR = 110
    override fun onInsertImageClicked() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_IMAGE_SELECTOR
            )
            return
        } else openGallery()
    }

    override fun onInsertLinkClicked() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mdEditor.configureEditor(false,"start typing" , H3)
        controlBar.setEditor(mdEditor)
        controlBar.setEditorControlListener(this)

        getDraftBtn.setOnClickListener {
            val dm = mdEditor.draft
            Log.d("blog App",dm.toString())
            val json = Gson().toJson(dm)
            Log.d("blog App",json)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_IMAGE_SELECTOR -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(this,"Permission not granted to access images.",Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_SELECTOR) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.data != null) {
                        val filePath = getRealPathFromURI(contentUri = data.data!!)
                        mdEditor.insertImage(filePath)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()!!
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, REQUEST_IMAGE_SELECTOR)
    }
}
