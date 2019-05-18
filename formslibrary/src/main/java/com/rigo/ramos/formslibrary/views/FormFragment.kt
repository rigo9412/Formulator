package com.rigo.ramos.formslibrary.views


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputLayout

import com.rigo.ramos.formslibrary.R
import kotlinx.android.synthetic.main.fragment_form.*
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.*
import androidx.core.app.ActivityCompat
import com.rigo.ramos.formslibrary.model.*
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "form"
private const val REQUEST_LOCATION: Int = 10

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FormFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var form: Form? = null
    private var index = 0
    private var pendingIndex = 0
    lateinit var layoutParams : LinearLayout.LayoutParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            form = it.getParcelable(ARG_PARAM1)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
          layoutParams =  LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParams.setMargins(8, 8, 8, 8);


        return inflater.inflate(R.layout.fragment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SharedPreference(this.context!!)
        tvTitle.text = form?.title
        tvTitle.setTextColor(resources.getColor(pref.getColor(SharedPreference.COLOR_TEXT)!!))
        index = 0
        form?.fields?.forEach {

            when(it.type){

                TypeField.TEXT,TypeField.TEXT_DEC,TypeField.TEXT_NUM,TypeField.TEXT_HOURS,
                TypeField.TEXT_EMAIL,TypeField.TEXT_PHONE,TypeField.TEXT_PASSWORD,TypeField.TEXT_DATE,TypeField.TEXT_RFC,TypeField.TEXT_CURP ->{
                    val v = it.createView(this.context!!,index)
                    val edt = v as TextInputLayout
                    //edt.editText?.onFocusChangeListener = this
                    edt.editText?.tag = index
                    if(it.value!!.size > 0)
                        edt.editText?.setText(it.value?.get(0)!!)

                    container.addView(v,layoutParams)
                }
                TypeField.SELECT_OPTION, TypeField.SELECT_RAD ->{
                    val v2 = it.createView(this.context!!,index)
                    val v3 = it.createTextviewLabel(context!!)
                    container.addView(v3,layoutParams)
                    container.addView(v2,layoutParams)
                }
                TypeField.LAYOUT_ADDRESS->{
                    pendingIndex = index
                    if(validatePermission(form?.permission!!)){
                        val v3 = it.createView(this.context!!,index)
                        container.addView(v3,layoutParams)
                    }
                }
                TypeField.SELECT_IMAGE->{
                    val v3 = it.createView(this.requireActivity(),index)
                    container.addView(v3)
                }

            }


            index++
        }



        
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECCIONAR_FOTO -> if (resultCode == Activity.RESULT_OK) {
                // try {

                try {

                    val uri = data?.data
                    val realPath = ImageFilePath.getPath(this.context!!, data?.data!!)
                    Log.i("BOTTOM_SHEETT", "onActivityResult: file path : $realPath")

                    val exif = ExifInterface(realPath)
                    val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                    val orientation = if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
                    val bitmap = MediaStore.Images.Media.getBitmap(this.context?.contentResolver, uri)
                    val photoFile = Picture(Picture.rotateBitmap(bitmap!!, orientation))
                    photoFile.path= realPath

                    val index = SaveStatus.getCurrentIndex(this.context!!)
                    if (index > -1)
                        this.setImage(index,photoFile)


                } catch (e: IOException) {
                    e.printStackTrace()
                }



            }
            TOMAR_FOTO -> if (resultCode == Activity.RESULT_OK) {

                try {
                    val index = SaveStatus.getCurrentIndex(this.context!!)
                    if(index > -1) {
                        val photoFile = (this.form!!.fields[index] as FieldImage).picture
                        val exif = ExifInterface(photoFile.path)
                        val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        val orientation =
                            if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.context?.contentResolver,
                            Uri.fromFile(photoFile.file)
                        )

                        photoFile.bitmap = Picture.rotateBitmap(bitmap, orientation)


                        this.setImage(index, photoFile)
                    }

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



   /* override fun onFocusChange(v: View?, hasFocus: Boolean) {
       Log.e("PATH-TAG",v?.tag.toString())

        if(v?.tag.toString().toInt()+1 == form?.fields?.size){

            for( i in 0 until container.childCount) {

                val view = container.getChildAt(i)
                val index = view.tag as Int?

                if (index != null) {

                    when (form!!.fields[index].type) {
                        TypeField.TEXT, TypeField.TEXT_DEC, TypeField.TEXT_NUM,
                        TypeField.TEXT_EMAIL, TypeField.TEXT_PHONE, TypeField.TEXT_PASSWORD, TypeField.TEXT_DATE,TypeField.TEXT_RFC,TypeField.TEXT_CURP -> {

                            if (view is TextInputLayout) {
                                view.error = null
                                view.isErrorEnabled = false
                            }
                        }
                        TypeField.SELECT_OPTION -> {
                            //NADA
                        }
                        TypeField.SELECT_RAD -> {
                            //NADA
                        }
                        TypeField.LAYOUT_ADDRESS -> {

                            if (view is LinearLayout) {
                                val view = container.getChildAt(i) as LinearLayout
                                val edt1 = view.getChildAt(2) as TextInputLayout
                                val edt2 = view.getChildAt(3) as TextInputLayout
                                val edt3 = view.getChildAt(4) as TextInputLayout
                                val edt4 = view.getChildAt(5) as TextInputLayout
                                val edt5 = view.getChildAt(6) as TextInputLayout

                                edt1.error = null
                                edt1.isErrorEnabled = false
                                edt2.error = null
                                edt2.isErrorEnabled = false
                                edt3.error = null
                                edt3.isErrorEnabled = false
                                edt4.error = null
                                edt4.isErrorEnabled = false
                                edt5.error = null
                                edt5.isErrorEnabled = false
                            }
                        }
                    }


                }
            }

            listener?.lastField()

       }
    }*/


    fun setImage(index: Int , picture: Picture){
        if(this.form?.fields!![index].type == TypeField.SELECT_IMAGE){
            (this.form?.fields!![index] as FieldImage).picture = picture
            this.form?.fields!![index].value!!.add(picture.getValue().toString())
            val iv = this.view?.findViewWithTag<ImageView>(index)
            iv?.setImageBitmap(picture.bitmap)

        }
    }

    fun validateForm():Form?{
        var isValid = true

        for( i in 0 until  container.childCount) {

            val view = container.getChildAt(i)
            val index = view.tag as Int?
            if (index != null) {
                when (form!!.fields[index].type) {
                    TypeField.TEXT, TypeField.TEXT_DEC, TypeField.TEXT_NUM, TypeField.TEXT_EMAIL,TypeField.TEXT_HOURS,
                    TypeField.TEXT_PHONE, TypeField.TEXT_PASSWORD, TypeField.TEXT_DATE,TypeField.TEXT_RFC,TypeField.TEXT_CURP -> {
                        if (view is TextInputLayout) {
                            form!!.fields[index].value?.add(0,view.editText?.text.toString())
                            if (!form!!.fields[index].isValid()) {
                                view.error = form!!.fields[index].errorMessage
                                isValid = false
                            }else{
                                view.error = null
                                view.isErrorEnabled = false

                            }
                        }
                    }
                    TypeField.SELECT_OPTION -> {
                        if (view is Spinner) {
                            form!!.fields[index].value?.add(0,view.selectedItem.toString())
                        }
                    }
                    TypeField.SELECT_RAD -> {
                        if (view is RadioGroup) {
                            val idcheck = view.checkedRadioButtonId
                            val viewC = view.findViewById<RadioButton>(idcheck)
                            form!!.fields[index].value?.add(0,viewC.text.toString())
                        }
                    }
                    TypeField.LAYOUT_ADDRESS -> {
                        if (view is LinearLayout) {

                            val edt1 = view.getChildAt(2) as TextInputLayout
                            val edt2 = view.getChildAt(3) as TextInputLayout
                            val edt3 = view.getChildAt(4) as TextInputLayout
                            val edt4 = view.getChildAt(5) as TextInputLayout
                            val edt5 = view.getChildAt(6) as TextInputLayout


                            form!!.fields[index].value?.add(0,edt1.editText?.text.toString())
                            form!!.fields[index].value?.add(1,edt2.editText?.text.toString())
                            form!!.fields[index].value?.add(2,edt3.editText?.text.toString())
                            form!!.fields[index].value?.add(4,edt4.editText?.text.toString())
                            form!!.fields[index].value?.add(5,edt5.editText?.text.toString())

                            if (form!!.fields[index].isValid()) {

                                if(edt1.editText?.text.isNullOrBlank()) {
                                    edt1.error = form!!.fields[index].errorMessage
                                    isValid = false
                                }else{
                                    edt1.error = null
                                    edt1.isErrorEnabled = false

                                }
                                if (edt2.editText?.text.isNullOrBlank() && form!!.fields[index].required) {
                                    edt2.error = form!!.fields[index].errorMessage
                                    isValid = false
                                }else{
                                    edt2.error = null
                                    edt2.isErrorEnabled = false

                                }

                                if (edt3.editText?.text.isNullOrBlank() && form!!.fields[index].required) {
                                    edt3.error = form!!.fields[index].errorMessage
                                    isValid = false
                                }else{
                                    edt3.error = null
                                    edt3.isErrorEnabled = false

                                }

                                if (edt4.editText?.text.isNullOrBlank() && form!!.fields[index].required) {
                                    edt4.error = form!!.fields[index].errorMessage
                                    isValid = false
                                }else{
                                    edt4.error = null
                                    edt4.isErrorEnabled = false

                                }

                                if (edt5.editText?.text.isNullOrBlank() && form!!.fields[index].required) {
                                    edt5.error = form!!.fields[index].errorMessage
                                    isValid = false
                                }else{
                                    edt5.error = null
                                    edt5.isErrorEnabled = false
                                }
                            }




                        }
                    }


                }

            }
        }



        Log.e("VALID-FORM",isValid.toString())

        return if(isValid)
            form
        else
            null


    }




    private fun validatePermission(permission:TypePermission):Boolean{
        when(permission){
            TypePermission.CAMERA->{
                //TODO CAMARA PERMISO
            }
            TypePermission.LOCATION->{
                if (ActivityCompat.checkSelfPermission(context!!,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION)
                } else {
                    return true
                }
            }
        }

        return false
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> {
                Log.e("test", "0")
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    val v3 = form!!.fields[pendingIndex].createView(this.context!!,pendingIndex)
                    container.addView(v3,0)
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }



    interface OnInteractionFormListner{
        fun lastField()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(form: Form) =
                FormFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, form)

                    }
                }
    }
}
