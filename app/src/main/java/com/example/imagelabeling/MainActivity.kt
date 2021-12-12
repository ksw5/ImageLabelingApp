package com.example.imagelabeling

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.example.myapplication.BuildConfig
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions


class MainActivity : AppCompatActivity() {
    private val viewModel: ImageViewModel by viewModels()
    val REQUEST_IMAGE_CAPTURE = 1


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cameraButton.setOnClickListener {
            // launch camera app
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }

        binding.randomButton.setOnClickListener {
            viewModel.getRandomPhoto()
            viewModel.apiResponse.observe(this,
                {
                    Glide.with(this)
                        .load(it.urls.regular)
                        .into(binding.imageView)

                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
            //prepare bitmap for mlkit
            val inputForMlkit = InputImage.fromBitmap(imageBitmap, 0)
            //initialize image labeling api
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(inputForMlkit)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    Log.i("Kieran", "Successfully proccessed")
                    var result = ""
                    for (label in labels) {
                        result = result + "\n" + label.text + " - " + label.confidence
                        binding.textView.text = result

                        Log.i("Kieran", result)
                    }

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Kieran", "Error processing")
                }
            val randomImage = viewModel.apiResponse.value?.urls?.regular as Bitmap
            binding.imageView.setImageBitmap(randomImage)
            val randomInputImage = InputImage.fromBitmap(randomImage, 0)
            val randomLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            randomLabeler.process(randomInputImage)
                .addOnSuccessListener { labels ->
                    Log.i("Kieran", "Successfully proccessed")
                    var result = ""
                    for (label in labels) {
                        result = result + "\n" + label.text + " - " + label.confidence
                        binding.textView.text = result

                        Log.i("Kieran", result)
                    }

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Kieran", "Error processing")
                }

        }



    }


}


