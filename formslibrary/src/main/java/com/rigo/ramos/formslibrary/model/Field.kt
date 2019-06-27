package com.rigo.ramos.formslibrary.model


import android.content.Context
import android.view.View
import android.widget.*
import com.rigo.ramos.formslibrary.R
import android.os.*
import kotlin.collections.ArrayList


abstract class Field :Parcelable{

    var id:ArrayList<String>? = null
    var title:String? = null
    var titleSection: String? = null
    var upperCase:Boolean? = null
    var required:Boolean = false
    var value:ArrayList<String>? = null
    var errorMessage: String? = null
    lateinit var type:TypeField

    abstract fun createView(context:Context, index: Int) : View?

    abstract fun isValid():Boolean

    fun createTextviewLabel(context: Context) : TextView{
        val textView =  TextView(context)
        if(type == TypeField.TEXT)
            textView.text = titleSection
        else
            textView.text = title
        //textView.setTextAppearance(context,R.style.TextView_InputLabel)
        textView.setPadding(24,0,0,0)
        textView.setTextColor(context.resources.getColor(R.color.gray))
        return textView
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(id)
        parcel.writeString(title)
        parcel.writeValue(upperCase)
        parcel.writeByte(if (required) 1 else 0)
        parcel.writeString(errorMessage)
        parcel.writeString(this.type.name)
        parcel.writeList(value)
    }

    override fun describeContents(): Int {
        return 0
    }





}