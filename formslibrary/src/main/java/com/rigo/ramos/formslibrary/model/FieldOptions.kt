package com.rigo.ramos.formslibrary.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import com.rigo.ramos.formslibrary.R

class FieldOptions() : Field() {

    var options: ArrayList<String>? = null

    constructor(parcel: Parcel) : this() {
        id  = parcel.readArrayList(String::class.java.classLoader) as java.util.ArrayList<String>?
        title = parcel.readString()!!
        upperCase = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        required = parcel.readByte() != 0.toByte()
        errorMessage = parcel.readString()
        type = TypeField.valueOf(parcel.readString()!!)
        value = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        options = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
    }


    constructor(id: ArrayList<String>, title: String, type: TypeField,
                options: ArrayList<String>?, value: ArrayList<String>? = arrayListOf()
    ) : this() {
        this.id = id
        this.title = title
        this.value = value
        this.options = options
        this.type = type
    }

    override fun createView(context: Context, index: Int): View? {
        return when (type) {
            TypeField.SELECT_OPTION -> createSpinner(index, context)
            TypeField.SELECT_RAD -> createRadioButtonOptions(index, context)
            else -> return null
        }
    }


    fun createSpinner(index: Int?, context: Context): Spinner {
        val li = LayoutInflater.from(context)
        val spinner = li.inflate(R.layout.template_spinner, null) as Spinner
        val adapter = ArrayAdapter(context, R.layout.item_spinner, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.tag = index
        var defaultValue = 0

        if(value?.size!! > 0) {
            for (i in 0 until options!!.size) {
                if (value!![0] == options!![i])
                    defaultValue = i
            }
        }

        spinner.setSelection(defaultValue)


        return spinner
    }

    fun createRadioButtonOptions(index: Int?, context: Context): RadioGroup {

        val rg = RadioGroup(context) //create the RadioGroup
        val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 16, 16, 16)
        rg.tag = index
        var defaultValue = 0
        val haveValue = value?.size!! > 0

        rg.orientation = RadioGroup.VERTICAL//or RadioGroup.VERTICAL
        for (i in 0 until options!!.size) {
            val rad = RadioButton(context)
            rad.id = i
            rad.text = options!![i]
            rad.textSize = 18f
            rad.layoutParams = params
            rad.setPadding(8, 8, 8, 8)
            if(haveValue && value!![0] == options!![i])
                defaultValue = i

            rg.addView(rad)
        }

        if (rg.childCount > 0) {
            rg.check(rg.getChildAt(defaultValue).id)
        }
        /*rg.setOnCheckedChangeListener { group, checkedId ->
            Log.e("RAD-ID",checkedId.toString())
            value = options!![checkedId]
        }*/

        return rg
    }

    override fun isValid(): Boolean {
        return true
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //super.writeToParcel(parcel, flags)
        parcel.writeList(id)
        parcel.writeString(title)
        parcel.writeValue(upperCase)
        parcel.writeByte(if (required) 1 else 0)
        parcel.writeString(errorMessage)
        parcel.writeString(this.type.name)
        parcel.writeList(value)
        parcel.writeList(options)
    }

    companion object CREATOR : Parcelable.Creator<FieldOptions> {
        override fun createFromParcel(parcel: Parcel): FieldOptions {
            return FieldOptions(parcel)
        }

        override fun newArray(size: Int): Array<FieldOptions?> {
            return arrayOfNulls(size)
        }
    }

}