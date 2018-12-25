# Formulator
#Libreria para generar formularios (alpha)
Hasta este momento la libreria puede generar EditText, Spinner, RadioGroups, Formulario para la direccion, Formulario para obtener una imagen(proceso)

Tipos de campos soportados:
- TEXT
- TEXT_NUM
- TEXT_DEC
- TEXT_PASSWORD
- TEXT_EMAIL
- TEXT_PHONE
- TEXT_DATE
- TEXT_CURP
- TEXT_RFC
- SELECT_OPTION
- SELECT_RAD
- SELECT_IMAGE(alpha)
- LAYOUT_ADDRESS


### Implementacion

Paso 1
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Paso 2

```
dependencies {
	implementation 'com.github.rigo9412:Formulator:v1.0'
}

```

### Uso

```kotlin
  //SECCION 1
        val field1 = Field(arrayListOf("nombre"),"Nombre*",true, TypeFied.SELECT_IMAGE,"Campo requerido",140)
        val field2 = Field(arrayListOf("apellido_paterno"),"Apellido Paterno*",true, TypeFied.TEXT,"Campo requerido",140)
        val field3 = Field(arrayListOf("apellido_materno"),"Apellido Materno*",true, TypeFied.TEXT,"Campo requerido",140)
        val form = Form("1","Informacion Personal", arrayListOf(field1,field2,field3),R.color.colorGreen,R.drawable.ic_person_black_24dp)
        //SECCION 2

        val field4 = Field(arrayListOf("sexo"),"Sexo",false, TypeFied.SELECT_RAD, arrayListOf("Hombre","Mujer"))
        val form2 = Form("1","Informacion Personal", arrayListOf(field4),R.color.colorGreen,R.drawable.ic_person_black_24dp)

        //SECCION 3
        val field5 = Field(arrayListOf("fecha_nacimiento"),"Fecha Nacimiento",true, TypeFied.TEXT_DATE,"Campo requerido")
        val field6 = Field(arrayListOf("estado_civil"),"Estado Civil",false, TypeFied.SELECT_OPTION,arrayListOf("Soltero(a)","Casado(a)"))
        val form3 = Form("1","Informacion Personal", arrayListOf(field5,field6),R.color.colorGreen,R.drawable.ic_person_black_24dp)

        //SECCION 4
        val field7 = Field(arrayListOf("telefono1"),"Telefono 1*",true, TypeFied.TEXT_PHONE,"Campo requerido",15)
        val field8 = Field(arrayListOf("telefono2"),"Telefono 2",false, TypeFied.TEXT_PHONE,"Campo requerido",15)
        val form4 = Form("1","Informacion de contacto", arrayListOf(field7,field8),R.color.colorGreen,R.drawable.ic_person_black_24dp)

        //SECCION 5
        val field9 = Field(arrayListOf("calle","colonia","estado","municipio","codigo_postal"),"Direccion*",false, TypeFied.LAYOUT_ADDRESS,"Campo requerido")
        val form5 = Form("1","Domicilio", arrayListOf(field9),R.color.colorGreen,R.drawable.ic_person_black_24dp,TypePermission.LOCATION)

        //Seccion 6
        val field10 = Field(arrayListOf("nacionalidad"),"Nacionalidad*",false, TypeFied.SELECT_OPTION, arrayListOf("Mexicana","Colombiano","otro"))
        val field11 = Field(arrayListOf("curp"),"CURP*",true, TypeFied.TEXT,"Campo requerido")
        val field12 = Field(arrayListOf("rfc"),"RFC",false, TypeFied.TEXT,"Campo requerido")
        val form6 = Form("1","Informacion Legal", arrayListOf(field10,field11,field12),R.color.colorGreen,R.drawable.ic_person_black_24dp)
	
	
	
//EN TU ACTIVITY

 val i = Intent(binding.root.context, FormsActivity::class.java)
                i.putExtra(FormsActivity.EXTRA_FORMS, item.forms)
                startActivity(i)
		
//Esto regresa los datos en JSON como resultado de la activity		


```


