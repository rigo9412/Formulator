package com.rigo.ramos.formslibrary.views

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rigo.ramos.formslibrary.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.picker_image.*
import android.provider.MediaStore.Images.Media.getBitmap
import android.provider.MediaStore
import android.graphics.Bitmap
import android.media.ExifInterface

import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.net.Uri

import android.util.Log
import com.rigo.ramos.formslibrary.model.ImageFilePath
import com.rigo.ramos.formslibrary.model.Picture
import java.io.IOException
import androidx.core.app.ActivityCompat.startActivityForResult
import android.app.Activity
import androidx.core.content.FileProvider
import com.rigo.ramos.formslibrary.BuildConfig
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.core.content.ContextCompat
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.StrictMode


const val SELECCIONAR_FOTO = 14
const val TOMAR_FOTO = 12
const val REQUEST_ALL_PERMIISON = 19
open class OpcionPickerImageDialog : BottomSheetDialogFragment() {

    var photoFile:Picture? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.picker_image, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          val builder = StrictMode.VmPolicy.Builder();
          StrictMode.setVmPolicy(builder.build());


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        tvSelectImage.setOnClickListener {
            openGallery(this.activity!!)
        }

        tvTakePicture.setOnClickListener {
            photoFile = Picture()
            openCamera(this.activity!!,photoFile!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if(checkPermissions()){
            tvTakePicture.isEnabled = false
            tvSelectImage.isEnabled = false
            requestPermissions(arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_ALL_PERMIISON)
        }else{
            tvTakePicture.isEnabled = true
            tvSelectImage.isEnabled = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECCIONAR_FOTO -> if (resultCode == RESULT_OK) {
                // try {

                    try {

                        val uri = data?.data
                        val realPath = ImageFilePath.getPath(this.context!!, data?.data!!)
                        Log.i("BOTTOM_SHEETT", "onActivityResult: file path : $realPath")

                        val exif = ExifInterface(realPath)
                        val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        val orientation = if (orientString != null) Integer.parseInt(orientString!!) else ExifInterface.ORIENTATION_NORMAL
                        val bitmap = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
                        photoFile = Picture(Picture.rotateBitmap(bitmap!!, orientation))
                        photoFile?.path= realPath


                        (this.context as FormFragment ).setImage(arguments?.getInt(INDEX)!!,photoFile!!)
                        this.dismiss()
                        //TODO: SEND RESULT TO PARENT
                        //ivProfile.setImageBitmap(photoFile.getPicture().getBitmap())

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }



            }
            TOMAR_FOTO -> if (resultCode == RESULT_OK) {

                try {
                    val exif = ExifInterface(photoFile?.path)
                    val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    val orientation = if (orientString != null) Integer.parseInt(orientString!!) else ExifInterface.ORIENTATION_NORMAL
                    val bitmap = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, Uri.fromFile(photoFile?.file))

                    photoFile?.bitmap = Picture.rotateBitmap(bitmap,orientation)
                    (this.context as FormFragment ).setImage(arguments?.getInt(INDEX)!!,photoFile!!)
                    this.dismiss()
                    //photoFile.setPicture(Picture(Picture.rotateBitmap(bitmap, orientation)))

                    //ivProfile.setImageBitmap(photoFile.getPicture().getBitmap())
                    // presenter.addNewPicture(photoFile);


                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }
            else -> {
                Log.e("Request Code", requestCode.toString() + "")
                Log.e("result", resultCode.toString() + "")
            }
        }

    }


    private fun openCamera(activity: Activity, photoFile: Picture) {
        try {
            photoFile.createImageFile()

            val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
                // Continue only if the File was successfully created

                    val photoURI = FileProvider.getUriForFile(activity,
                            "com.rigo.ramos.formslibrary.fileprovider",
                            photoFile.file!!)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //      takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //     takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    activity.startActivityForResult(takePictureIntent, TOMAR_FOTO)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun checkPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.CAMERA)
        val writeExternalStorage = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readExternalStorage = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        return camera != PackageManager.PERMISSION_GRANTED && writeExternalStorage != PackageManager.PERMISSION_GRANTED && readExternalStorage != PackageManager.PERMISSION_GRANTED

    }

    private fun openGallery(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        activity.startActivityForResult(Intent.createChooser(galleryIntent, activity.resources.getString(R.string.message_select_option_picture)), SELECCIONAR_FOTO)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_ALL_PERMIISON){
            if(grantResults.isNotEmpty()){
                val camera = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val storageW = grantResults[1] === PackageManager.PERMISSION_GRANTED
                val location = grantResults[2] === PackageManager.PERMISSION_GRANTED
                val storageR = grantResults[3] === PackageManager.PERMISSION_GRANTED

                if (camera && storageR && storageW && location){
                    tvTakePicture.isEnabled = true
                    tvSelectImage.isEnabled = true


                }else{
                    tvTakePicture.isEnabled = false
                    tvSelectImage.isEnabled = false
                }
            }
        }
    }


    companion object {
        const val INDEX = "index"
        fun newInstance(index: Int): OpcionPickerImageDialog = OpcionPickerImageDialog().apply {
            arguments = Bundle().apply {
                putInt(INDEX, index)
            }
        }

    }

}