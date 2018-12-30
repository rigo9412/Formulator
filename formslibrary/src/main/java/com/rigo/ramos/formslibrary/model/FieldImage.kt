package com.rigo.ramos.formslibrary.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.R
import com.rigo.ramos.formslibrary.model.SaveStatus.Companion.setCurrentIndex
import com.rigo.ramos.formslibrary.views.OpcionPickerImageDialog
import java.util.ArrayList

class FieldImage(): Field(){

    var picture: Picture = Picture()

    constructor(parcel: Parcel) : this() {
        id = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        title = parcel.readString()!!
        upperCase = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        required = parcel.readByte() != 0.toByte()
        errorMessage = parcel.readString()
        type = TypeFied.valueOf(parcel.readString()!!)
        value = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
    }


    constructor(id: ArrayList<String>, title:String, required: Boolean,errorMessage: String, type:TypeFied, value: ArrayList<String>? = arrayListOf()): this(){
        this.id = id
        this.title = title
        this.required = required
        this.errorMessage = errorMessage
        this.value = value
        this.type = type
    }

     override fun createView(context:Context, index: Int) : View? {
        return when(type){
            TypeFied.SELECT_IMAGE -> createPictureCapture(index,context)
            else -> null
        }
    }

    fun createPictureCapture(index: Int?,context: Context): ImageView {
        val li = LayoutInflater.from(context)
        val iv = li.inflate(R.layout.template_imageview, null) as ImageView
        iv.tag = index
        iv.setOnClickListener {
            picture.createImageFile()
            setCurrentIndex(context,index!!)
            val bottom = OpcionPickerImageDialog.newInstance(picture.path!!)
            bottom.show((context as AppCompatActivity).supportFragmentManager,bottom.tag)
        }

        return iv
    }

    override fun isValid(): Boolean {
      return value?.get(0)!!.isNotEmpty() && required
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

    companion object CREATOR : Parcelable.Creator<FieldImage> {
        override fun createFromParcel(parcel: Parcel): FieldImage {
            return FieldImage(parcel)
        }

        override fun newArray(size: Int): Array<FieldImage?> {
            return arrayOfNulls(size)
        }
    }


}