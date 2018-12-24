package com.rigo.ramos.formslibrary.model

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
@Parcelize
class Form(val id:String,val title: String, var fields: ArrayList<Field>, val color:Int , val icon: Int,val permission: TypePermission = TypePermission.NONE): Parcelable {



    fun generateJsonResult(): JSONObject{
        val o = JSONObject()
        val gson = Gson()
        fields.forEach {

            if(it.value?.size == 1)
                o.put(it.id[0],it.value?.get(0))
            else{
                var index = 0
                it.id.forEach { id ->
                    o.put(id,it.value?.get(index))
                    index++
                }

            }


        }
        return o
    }


}