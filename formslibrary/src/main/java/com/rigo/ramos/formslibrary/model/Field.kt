package com.rigo.ramos.formslibrary.model


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rigo.ramos.formslibrary.R
import java.util.regex.Pattern
import android.widget.RadioButton
import android.widget.RadioGroup
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.location.*
import android.os.*
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.views.FixedHoloDatePickerDialog
import com.rigo.ramos.formslibrary.views.OpcionPickerImageDialog
import com.rigo.ramos.formslibrary.views.SingleShotLocationProvider
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


abstract class Field :Parcelable{

     var id:ArrayList<String>? = null
     var title:String? = null
    var upperCase:Boolean? = null
    var required:Boolean = false
    var value:ArrayList<String>? = null
    var errorMessage: String? = null
    lateinit var type:TypeFied

    abstract fun createView(context:Context, index: Int) : View?

    abstract fun isValid():Boolean

    fun createTextviewLabel(context: Context) : TextView{
        val textView =  TextView(context)
        textView.text = title
        textView.setTextAppearance(context,R.style.TextView_InputLabel)
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