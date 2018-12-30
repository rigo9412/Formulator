package com.rigo.ramos.formslibrary.model

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
class Form(): Parcelable {


    var id:String? = ""
    var title: String? = ""
    var fields: ArrayList<Field> = arrayListOf()
    var color: Int = 0
    var icon: Int = 0
    var permission: TypePermission = TypePermission.NONE

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        title = parcel.readString()
        color = parcel.readInt()
        icon = parcel.readInt()
        permission = TypePermission.valueOf(parcel.readString()!!)
        fields = parcel.readArrayList(Field::class.java.classLoader) as ArrayList<Field>
    }

    constructor(id:String,title: String,fields: ArrayList<Field>,permission: TypePermission = TypePermission.NONE):this(){
        this.id = id
        this.title = title
        this.fields = fields
        this.permission = permission
    }

    constructor(id:String,title: String,fields: ArrayList<Field>,permission: TypePermission = TypePermission.NONE,color:Int ,icon: Int):this(){
        this.id = id
        this.title = title
        this.fields = fields
        this.permission = permission
        this.color = color
        this.icon = icon
    }

    fun generateJsonResult(): JSONObject{
        val o = JSONObject()
        val gson = Gson()
        fields.forEach {

            if(it.value?.size == 1)
                o.put(it.id!![0],it.value?.get(0))
            else{
                var index = 0
                it.id!!.forEach { id ->
                    o.put(id,it.value?.get(index))
                    index++
                }

            }


        }
        return o
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(id)
        dest.writeString(title)
        dest.writeInt(color)
        dest.writeInt(icon)
        dest.writeString(permission.name)
        dest.writeList(fields)
    }

    override fun describeContents(): Int {
         return 0
    }


    companion object CREATOR : Parcelable.Creator<Form> {
        override fun createFromParcel(parcel: Parcel): Form {
            return Form(parcel)
        }

        override fun newArray(size: Int): Array<Form?> {
            return arrayOfNulls(size)
        }
    }


}