package com.example.imagepicker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.imageclassificationkotlin.Classifier
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var imgV:ImageView
     lateinit var btn: Button
     lateinit var imageUri: Uri
     lateinit var classifier: Classifier
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initializing classifier
        classifier = Classifier(assets,"model_unquant.tflite","labels.txt", 224)

        imgV = findViewById(R.id.imageView)
        btn = findViewById(R.id.button2)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri->
            if (uri != null) {
                imgV.setImageURI(uri)
                doInference()
            }
        }

        btn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        imageUri = createImageUri()
        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()){
            if (imageUri!=null) {
                imgV.setImageURI(imageUri)
                doInference()
            }
       }

        btn.setOnLongClickListener {
            takePicture.launch(imageUri)
            true
        }
    }

// The process of parsing input to the model, with this function

    fun doInference(){
      val result = classifier.recognizeImage(imgV.drawable.toBitmap())
        Log.d("Classifier",result.toString())
    }

    fun createImageUri():Uri {
        val image = File(applicationContext.filesDir, "camera_photo.png")

        return FileProvider.getUriForFile(applicationContext,"com.example.imagepicker1.fileprovider",image)
    }
}