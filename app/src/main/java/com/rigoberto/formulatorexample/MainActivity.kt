package com.rigoberto.formulatorexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.model.Field
import com.rigo.ramos.formslibrary.model.FieldImage
import com.rigo.ramos.formslibrary.model.Form
import com.rigo.ramos.formslibrary.model.TypeFied
import com.rigo.ramos.formslibrary.views.FormsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
            startActivity(i)
        }
    }
}
