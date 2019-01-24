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
import android.provider.MediaStore

import java.io.IOException
import android.app.Activity
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.os.StrictMode
import android.util.Log
import com.rigo.ramos.formslibrary.BuildConfig
import java.io.File


const val SELECCIONAR_FOTO = 14
const val TOMAR_FOTO = 12
const val REQUEST_ALL_PERMIISON = 19
open class OpcionPickerImageDialog : BottomSheetDialogFragment() {

    //var photoFile:Picture? = null

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
            val file = File(this.arguments!!.getString(PATH))
            openCamera(this.activity!!,file)
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






    private fun openCamera(activity: Activity, photoFile: File) {
        try {
           // photoFile.createImageFile()

            val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
                // Continue only if the File was successfully created
                    //com.rigoberto.formulatorexample.com
                    val photoURI = FileProvider.getUriForFile(activity, getActivity()?.applicationContext?.packageName+".com.rigo.ramos.formslibrary.fileprovider",
                            photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //      takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //     takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    activity.startActivityForResult(takePictureIntent, TOMAR_FOTO)

                this.dismiss()


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
        this.dismiss()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_ALL_PERMIISON){
            if(grantResults.isNotEmpty()){
                val camera = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val storageW = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val location = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val storageR = grantResults[3] == PackageManager.PERMISSION_GRANTED

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
        const val PATH = "path"
        fun newInstance(path: String): OpcionPickerImageDialog = OpcionPickerImageDialog().apply {
            arguments = Bundle().apply {
                putString(PATH, path)
            }
        }

    }

}