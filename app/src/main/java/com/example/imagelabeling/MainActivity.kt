package com.example.imagelabeling

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
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
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.view.drawToBitmap
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class MainActivity : AppCompatActivity() {
    private val viewModel: ImageViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cameraButton.setOnClickListener {
            // launch camera app
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureResultLauncher.launch(takePictureIntent)
        }
        binding.randomButton.setOnClickListener {
            viewModel.getRandomPhoto()
            viewModel.apiResponse.observe(this, {
                Glide.with(this)
                    .asBitmap()
                    .load(it.urls.regular)
                    .into(binding.imageView)

            })
            val imageBitmap = binding.imageView.drawToBitmap()
            val imageForMlKit = InputImage.fromBitmap(imageBitmap, 0)
            detectObject(imageForMlKit)


        }

        //val imageDrawable = binding.imageView.setImageDrawable(binding.imageView.drawable)
        //val imageBitmap = binding.imageView.setImageBitmap(imageDrawable as Bitmap)
        //val imageBitmap = binding.imageView as Bitmap


    }


    var takePictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(imageBitmap)
                //Grab the photo taken as a drawable and convert to bitmap
                val bitmap = (binding.imageView.getDrawable() as BitmapDrawable).bitmap
                //Rotate the bitmap from camera so that it's right side up
                val rotatedBitmap = bitmap.rotate(90f)
                //prepare bitmap for mlkit
                val imageForMlkit = InputImage.fromBitmap(rotatedBitmap, 0)
                //reset imageview to rotated bitmap
                binding.imageView.setImageBitmap(rotatedBitmap)
                //initialize image labeling api
                detectObject(imageForMlkit)
            }
        }

    fun detectObject(imageForMlKit: InputImage) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        labeler.process(imageForMlKit)
            .addOnSuccessListener { labels ->
                Log.i("Kieran", "Successfully proccessed")
                var result = ""
                for (label in labels) {
                    result = "\n" + result + "\n" + label.text + " - " + label.confidence
                    binding.textView.text = result

                    Log.i("Kieran", result)
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
                alertDialogBuilder.setTitle("You are not connected!")
                alertDialogBuilder.setMessage("Please connect to the internet")
                alertDialogBuilder.create()
                alertDialogBuilder.show()
                Log.e("Kieran", "Error processing")
            }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

}



