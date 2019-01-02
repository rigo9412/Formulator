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
		        implementation 'com.github.rigo9412:Formulator:1.0.1'
}

```

### Uso

```kotlin
 val field1 = FieldImage(arrayListOf("nombre"),"Nombre*",true,"Campo requerido", TypeFied.SELECT_IMAGE)
            val form = Form("1","Informacion Personal", arrayListOf(field1))


	
//EN TU ACTIVITY


            val i = Intent(this, FormsActivity::class.java)
            i.putExtra(FormsActivity.EXTRA_FORMS, arrayListOf(form))
	    startActivity(i)
	
//Esto regresa los datos en JSON como resultado de la activity		


```


