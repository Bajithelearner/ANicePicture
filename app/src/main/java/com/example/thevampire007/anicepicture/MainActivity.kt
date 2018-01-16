package com.example.thevampire007.anicepicture


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val URL_DATA = "https://api.unsplash.com/photos/random/?client_id=64d845197ea9cbe7c09c652872bc96640c7a7bc28854b6d132b9d2a6c6211390"
    private lateinit var refreshButton: Button
    private lateinit var image: ImageView
    private lateinit var uname :TextView
    private lateinit var bdownload : Button
    private lateinit var blike : Button
    lateinit var userurl: String
    var lflag = 0
    var dflag=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshButton = findViewById(R.id.brefresh) as Button
        image = findViewById(R.id.imageView) as ImageView

        uname = findViewById(R.id.nameTV) as TextView

        bdownload = findViewById(R.id.bdownload) as Button
        blike = findViewById(R.id.blike)

        refreshButton.setOnClickListener{
            lflag=0
            dflag=0
            internetPermission()
             setUpImage()

        }

        uname.setOnClickListener{

        }

        blike.setOnClickListener{
            val b = blike.text

            if(lflag == 0) {
                if (b != "Like" && b != "LIKE") {
                    var i = Integer.parseInt(b.substring(5))
                    i++
                    blike.text = "Like " + i

                } else {
                    val i = 1
                    blike.text = "Like " + i
                }
                lflag=1
            }
            else
            {
                Toast.makeText(applicationContext,"You Have Already Liked this" ,Toast.LENGTH_SHORT).show()
            }
                    }

        bdownload.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                var b = bdownload.text as String
                if (dflag == 0) {
                    if (b != "Save" && b != "Save") {
                        var i = Integer.parseInt(b.substring(5))
                        i++
                        bdownload.text = "Save " + i
                        downloadImageEx()


                    } else {
                        var i = 1
                        bdownload.text = "Save " + i
                        downloadImageEx()
                    }

                } else {
                    Toast.makeText(applicationContext, "You Have Already Downloaded this", Toast.LENGTH_SHORT).show()
                }
            }else
            {
                storagePermission()
                Toast.makeText(applicationContext, "You need to give the permission", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun downloadImageEx()
    {

        if(isExternalStorageWritable()) {

            val b = (image.drawable as BitmapDrawable).bitmap as Bitmap
            //var store = filesDir as File
            val fileOutputStream: FileOutputStream? = null
            try {
                val timestamp = Date().time.toString()
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),timestamp+".jpg")
                //file.mkdir()

                val fileOutputStream = FileOutputStream(file)
                b.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                dflag = 1
            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext, "File Not FOund", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "IOEX", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                fileOutputStream?.close()
            }
        }else
        {
            Toast.makeText(applicationContext, "Not available" + applicationContext.filesDir.absolutePath.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    fun isExternalStorageWritable() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


    private fun setUpImage()
    {



        var stringRequest = StringRequest(Request.Method.GET,URL_DATA,
                Response.Listener<String>{ response ->
                    try {
                        val jsonresponse = JSONObject(response)
                        val urlArrayObject = jsonresponse.getJSONObject("urls")
                        val url = if(urlArrayObject.has("regular")) urlArrayObject.getString("regular") as String
                                    else ""

                        val likes = jsonresponse.getString("likes") as String
                        val username = jsonresponse.getJSONObject("user").getString("name") as String
                        userurl = jsonresponse.getJSONObject("user").getJSONObject("links").getString("html") as String
                        val downloads = jsonresponse.getString("downloads") as String
                        blike.text = "Like "+likes
                        bdownload.text = "Save "+ downloads


                        uname.text =username +": "+ userurl



                        Picasso.with(applicationContext).load(url).into(image)
                    }catch (e: Exception)
                    {

                        Toast.makeText(applicationContext,"Exception with Json!" ,Toast.LENGTH_SHORT).show()
                    }

                }

               , Response.ErrorListener {error ->
            Toast.makeText(applicationContext,error.toString(),Toast.LENGTH_SHORT).show()
        })

        var requestQueue = Volley.newRequestQueue(this) as RequestQueue
        requestQueue.add(stringRequest)

    }





    private fun storagePermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123)
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                        Toast.makeText(applicationContext,"Thank You!", Toast.LENGTH_SHORT).show()
                } else {



                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun internetPermission()
    {


    }
}
