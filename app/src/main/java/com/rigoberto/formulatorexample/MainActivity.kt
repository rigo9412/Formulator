package com.rigoberto.formulatorexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rigo.ramos.formslibrary.model.*
import com.rigo.ramos.formslibrary.views.FormsContainer
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(),ActionsListenerForm {
    override fun onSave(result: JSONObject) {
        Log.e("result",result.toString())

    }

    private val FORM_ACTIVITY = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnForm.setOnClickListener {
            //val field1 = FieldImage(arrayListOf("nombre"),"Nombre*",false,"Campo requerido", TypeField.SELECT_IMAGE)
            val fieldRazonSocial = FieldText(arrayListOf("razonSocial"), "RazonSocial",false, TypeField.TEXT,
                "Es requerido",150,true, arrayListOf() )
            val fieldTipoFlotilla = FieldOptions(arrayListOf("tipoFlotilla"),"Tipo flotilla",TypeField.SELECT_OPTION,
                arrayListOf("1","2","3"), arrayListOf())


            val field3 = FieldText(arrayListOf("apellido_paterno"),"Apellido Paterno*",false, TypeField.TEXT_PHONE,"Campo requerido",140)
            val field32 = FieldText(arrayListOf("apellido_paterno"),"Apellido Paterno*",false, TypeField.TEXT_PHONE,"Campo requerido",140)
            //val field3 = Field(arrayListOf("apellido_materno"),"Apellido Materno*",true, TypeField.TEXT,"Campo requerido",140)
            val form = Form("1","Informacion Personal", arrayListOf(fieldRazonSocial,fieldTipoFlotilla,field3))


            val i = Bundle()
            i.putParcelableArrayList(FormsContainer.EXTRA_FORMS, arrayListOf(form))
            i.putInt(FormsContainer.EXTRA_THEME,R.style.FullScreenDialogStyle)
            i.putInt(FormsContainer.EXTRA_BACKGROUND_COLOR,R.color.blue_normal)
            i.putInt(FormsContainer.EXTRA_TEXT_COLOR,R.color.white)
            i.putInt(FormsContainer.EXTRA_PRIMARY_COLOR,R.color.white)
            i.putInt(FormsContainer.EXTRA_ACCENT_COLOR,R.color.colorAccent)

            val dialog = FormsContainer()
            val ft = this.supportFragmentManager.beginTransaction()
            dialog.arguments = i
            dialog.show(ft, FormsContainer.TAG)

        }
    }

}
