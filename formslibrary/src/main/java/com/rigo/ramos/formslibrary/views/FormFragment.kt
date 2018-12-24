package com.rigo.ramos.formslibrary.views


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
import com.rigo.ramos.formslibrary.model.Form
import com.rigo.ramos.formslibrary.model.TypeFied
import com.rigo.ramos.formslibrary.model.TypePermission
import kotlinx.android.synthetic.main.fragment_form.*
import android.content.pm.PackageManager
import android.widget.*
import androidx.core.app.ActivityCompat
import com.rigo.ramos.formslibrary.model.Picture
import com.rigo.ramos.formslibrary.views.SingleShotLocationProvider.GPSCoordinates





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
    private var listener : OnInteractionFormListner? = null
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

        tvTitle.text = form?.title

        index = 0
        form?.fields?.forEach {
            //
            when(it.type){

                TypeFied.TEXT,TypeFied.TEXT_DEC,TypeFied.TEXT_NUM,
                TypeFied.TEXT_EMAIL,TypeFied.TEXT_PHONE,TypeFied.TEXT_PASSWORD,TypeFied.TEXT_DATE,TypeFied.TEXT_RFC,TypeFied.TEXT_CURP ->{
                    val v = it.createView(this.context!!,index)
                    val edt = v as TextInputLayout
                    //edt.editText?.onFocusChangeListener = this
                    edt.editText?.tag = index
                    if(it.value!!.size > 0)
                        edt.editText?.setText(it.value?.get(0)!!)

                    container.addView(v,layoutParams)
                }
                TypeFied.SELECT_OPTION, TypeFied.SELECT_RAD ->{
                    val v2 = it.createView(this.context!!,index)
                    val v3 = it.createTextviewLabel(context!!)
                    container.addView(v3,layoutParams)
                    container.addView(v2,layoutParams)
                }
                TypeFied.LAYOUT_ADDRESS->{
                    pendingIndex = index
                    if(validatePermission(form?.permission!!)){
                        val v3 = it.createView(this.context!!,index)
                        container.addView(v3,layoutParams)
                    }
                }
                TypeFied.SELECT_IMAGE->{
                    val v3 = it.createView(this.requireActivity(),index)
                    container.addView(v3,layoutParams)
                }

            }


            index++
        }



        
    }



   /* override fun onFocusChange(v: View?, hasFocus: Boolean) {
       Log.e("INDEX-TAG",v?.tag.toString())

        if(v?.tag.toString().toInt()+1 == form?.fields?.size){

            for( i in 0 until container.childCount) {

                val view = container.getChildAt(i)
                val index = view.tag as Int?

                if (index != null) {

                    when (form!!.fields[index].type) {
                        TypeFied.TEXT, TypeFied.TEXT_DEC, TypeFied.TEXT_NUM,
                        TypeFied.TEXT_EMAIL, TypeFied.TEXT_PHONE, TypeFied.TEXT_PASSWORD, TypeFied.TEXT_DATE,TypeFied.TEXT_RFC,TypeFied.TEXT_CURP -> {

                            if (view is TextInputLayout) {
                                view.error = null
                                view.isErrorEnabled = false
                            }
                        }
                        TypeFied.SELECT_OPTION -> {
                            //NADA
                        }
                        TypeFied.SELECT_RAD -> {
                            //NADA
                        }
                        TypeFied.LAYOUT_ADDRESS -> {

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnInteractionFormListner

    }


    fun setImage(index: Int , picture: Picture){
        this.form?.fields!![index].value = picture.getValues()
    }

    fun validateForm():Form?{
        var isValid = true

        for( i in 0 until  container.childCount) {

            val view = container.getChildAt(i)
            val index = view.tag as Int?
            if (index != null) {
                when (form!!.fields[index].type) {
                    TypeFied.TEXT, TypeFied.TEXT_DEC, TypeFied.TEXT_NUM, TypeFied.TEXT_EMAIL,
                    TypeFied.TEXT_PHONE, TypeFied.TEXT_PASSWORD, TypeFied.TEXT_DATE,TypeFied.TEXT_RFC,TypeFied.TEXT_CURP -> {
                        if (view is TextInputLayout) {
                            form!!.fields[index].value?.add(0,view.editText?.text.toString())
                            if (form!!.fields[index].isValid()) {
                                view.error = form!!.fields[index].errorMessage
                                isValid = false
                            }else{
                                view.error = null
                                view.isErrorEnabled = false

                            }
                        }
                    }
                    TypeFied.SELECT_OPTION -> {
                        if (view is Spinner) {
                            form!!.fields[index].value?.add(0,view.selectedItem.toString())
                        }
                    }
                    TypeFied.SELECT_RAD -> {
                        if (view is RadioGroup) {
                            val idcheck = view.checkedRadioButtonId
                            val viewC = view.findViewById<RadioButton>(idcheck)
                            form!!.fields[index].value?.add(0,viewC.text.toString())
                        }
                    }
                    TypeFied.LAYOUT_ADDRESS -> {
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