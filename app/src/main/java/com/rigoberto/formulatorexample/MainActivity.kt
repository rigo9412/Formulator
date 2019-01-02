package com.rigoberto.formulatorexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.model.Field
import com.rigo.ramos.formslibrary.model.FieldImage
import com.rigo.ramos.formslibrary.model.Form
import com.rigo.ramos.formslibrary.model.TypeFied
import com.rigo.ramos.formslibrary.views.FormsActivity
import com.rigo.ramos.formslibrary.views.RESULT_FORM
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val FORM_ACTIVITY = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnForm.setOnClickListener {
            val field1 = FieldImage(arrayListOf("nombre"),"Nombre*",true,"Campo requerido", TypeFied.SELECT_IMAGE)
            //val field2 = Field(arrayListOf("apellido_paterno"),"Apellido Paterno*",true, TypeFied.TEXT,"Campo requerido",140)
            //val field3 = Field(arrayListOf("apellido_materno"),"Apellido Materno*",true, TypeFied.TEXT,"Campo requerido",140)
            val form = Form("1","Informacion Personal", arrayListOf(field1))


            val i = Intent(this, FormsActivity::class.java)
            i.putExtra(FormsActivity.EXTRA_FORMS, arrayListOf(form))

            startActivityForResult(i,FORM_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == FORM_ACTIVITY){
            Log.e("RESULT-FORM",data?.getStringExtra(RESULT_FORM))
        }
    }
}
