package com.rigoberto.formulatorexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.model.*
import com.rigo.ramos.formslibrary.views.FormsActivity
import com.rigo.ramos.formslibrary.views.RESULT_FORM
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val FORM_ACTIVITY = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnForm.setOnClickListener {
            val field1 = FieldImage(arrayListOf("nombre"),"Nombre*",true,"Campo requerido", TypeField.SELECT_IMAGE)
            val field2 = FieldText(arrayListOf("hora"),"Hora de entrega*",true, TypeField.TEXT_HOURS,"Campo requerido")
            val fieldTipoFlotilla = FieldOptions(arrayListOf("tipoFlotilla"),"Tipo flotilla",TypeField.SELECT_OPTION,
                arrayListOf("1","2","3"), arrayListOf())


            //val field2 = Field(arrayListOf("apellido_paterno"),"Apellido Paterno*",true, TypeField.TEXT,"Campo requerido",140)
            //val field3 = Field(arrayListOf("apellido_materno"),"Apellido Materno*",true, TypeField.TEXT,"Campo requerido",140)
            val form = Form("1","Informacion Personal", arrayListOf(field1,field2,fieldTipoFlotilla))


            val i = Intent(this, FormsActivity::class.java)
            i.putExtra(FormsActivity.EXTRA_FORMS, arrayListOf(form))
            i.putExtra(FormsActivity.EXTRA_THEME,R.style.AppTheme)
            i.putExtra(FormsActivity.EXTRA_BACKGROUND_COLOR,R.color.colorBackgroud)
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
