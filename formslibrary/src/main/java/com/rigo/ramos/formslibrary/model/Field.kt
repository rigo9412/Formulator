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


class Field() :Parcelable{


    lateinit var id:ArrayList<String>
    lateinit var title:String
    var upperCase:Boolean? = null
    var required:Boolean = false
    var value:ArrayList<String>? = null
    var errorMessage: String? = null
    var maxLength: Int? = null
    var minDate: Long? = null
    var maxDate: Long? = null
    var options: ArrayList<String>? = null
    lateinit var type:TypeFied

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()!!
        upperCase = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        required = parcel.readByte() != 0.toByte()
        errorMessage = parcel.readString()
        maxLength = parcel.readValue(Int::class.java.classLoader) as? Int
        minDate = parcel.readValue(Long::class.java.classLoader) as? Long
        maxDate = parcel.readValue(Long::class.java.classLoader) as? Long
        type = TypeFied.valueOf(parcel.readString()!!)
        value = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        options = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
    }


    //(val id:ArrayList<String>, var title:String, val required: Boolean, var type:TypeFied, var errorMessage:String,var maxLength: Int?,var minDate: Long?,var maxDate: Long?, var value:ArrayList<String>?, var options: ArrayList<String>?)

    //CONSTRUCTOR PARA EDITTEXT
    constructor(id:ArrayList<String>,title:String,required: Boolean,type:TypeFied, errorMessage:String?,
                maxLength: Int? = null , upperCase: Boolean? = false,value:ArrayList<String>? = arrayListOf()): this(){
        this.id = id
        this.title = title
        this.required = required
        this.value = value
        this.errorMessage = errorMessage
        this.maxLength = maxLength
        this.upperCase = upperCase
        this.type = type
    }

    //CONSTRUCTOR DATE
    constructor(id:ArrayList<String>,title:String,required: Boolean,type:TypeFied, errorMessage:String?,
                minDate: Long?,maxDate: Long?,value:ArrayList<String>? = arrayListOf()):this(){
        this.id = id
        this.title = title
        this.required = required
        this.value = value
        this.errorMessage = errorMessage
        this.minDate = minDate
        this.maxDate = maxDate
        this.type = type

    }


    //CONSTRUCTOR SELECTION AND RADIOBUTTON
    constructor(id:ArrayList<String>,title:String,required: Boolean,type:TypeFied,
                options: ArrayList<String>?,value:ArrayList<String>? = arrayListOf()): this(){
        this.id = id
        this.title = title
        this.required = required
        this.value = value
        this.options = options
        this.type = type
    }
    //CONSTRUCTOR ADDRESS



    //todo: validaciones especificas
    fun createView(context:Context,index: Int) : View? {
        return when(type){
            TypeFied.TEXT-> createEditText(index,context,title,type,null,null,maxLength)
            TypeFied.TEXT_RFC-> createEditText(index,context,title,type)
            TypeFied.TEXT_CURP-> createEditText(index,context,title,type)
            TypeFied.TEXT_EMAIL-> createEditText(index,context,title,type)
            TypeFied.TEXT_PASSWORD-> createEditText(index,context,title,type)
            TypeFied.TEXT_NUM-> createEditText(index,context,title,type)
            TypeFied.TEXT_DEC-> createEditText(index,context,title,type)
            TypeFied.TEXT_PHONE-> createEditText(index,context,title,type)
            TypeFied.TEXT_DATE-> createEditText(index,context,title,type,minDate,maxDate)
            TypeFied.SELECT_OPTION-> createSpinner(index,context)
            TypeFied.SELECT_RAD -> createRadioButtonOptions(index,context)
            TypeFied.LAYOUT_ADDRESS -> createAddressButton(index,context)
            TypeFied.SELECT_IMAGE -> createPictureCapture(index,context)
        }
    }


    fun createEditText(index: Int?,context:Context,title:String,type:TypeFied,maxDate: Long? = null ,minDate: Long? = null,maxLength: Int? = null): TextInputLayout{


        val li = LayoutInflater.from(context)
        val layout = li.inflate(R.layout.template_text_input_layout, null) as TextInputLayout//TextInputLayout(ContextThemeWrapper(context,R.style.StyleEditText),null,R.style.StyleEditText)
        val editText = TextInputEditText(context)
        editText.hint=title
        layout.tag = index

        when(type){
            TypeFied.TEXT_PHONE -> {
                editText.inputType = InputType.TYPE_CLASS_PHONE
                editText.addTextChangedListener( PhoneNumberFormattingTextWatcher())
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(12)
                editText.filters = FilterArray

            }
            TypeFied.TEXT_EMAIL ->  editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TypeFied.TEXT_NUM ->  editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            TypeFied.TEXT_DEC ->  editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            TypeFied.TEXT_PASSWORD ->  {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            TypeFied.TEXT ->  editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
            TypeFied.TEXT_DATE-> {

                val pickerDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    value?.add(dayOfMonth.toString()+" / "+(month+1)+" / "+year)
                    layout.editText?.setText(value?.get(0))
                 }


                editText.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = calendar.get(Calendar.MONTH)
                    val year = calendar.get(Calendar.YEAR)

                    val dialog = FixedHoloDatePickerDialog(context, pickerDialog, year,month,day)

                    if(minDate != null)
                        dialog.datePicker.minDate = minDate
                    if(maxDate != null)
                        dialog.datePicker.maxDate = maxDate

                    dialog.datePicker.updateDate(year,month,day)

                    dialog.show()
                }
                editText.inputType = InputType.TYPE_NULL
                editText.isFocusable = false
                editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            TypeFied.TEXT_CURP->{
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps(),InputFilter.LengthFilter(18))

            }
            TypeFied.TEXT_RFC->{
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps(),InputFilter.LengthFilter(13))
            }

        }


        editText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        layout.addView(editText)
        return layout
    }


    fun createPictureCapture(index: Int?,context: Context): ImageView{
        val li = LayoutInflater.from(context)
        val iv = li.inflate(R.layout.template_imageview, null) as ImageView

        iv.setOnClickListener {
            val bottom = OpcionPickerImageDialog.newInstance()
            bottom.show((context as AppCompatActivity).supportFragmentManager,bottom.tag)
        }

        return iv
    }


    fun createSpinner(index: Int?,context: Context): Spinner{

        val li = LayoutInflater.from(context)
        val spinner = li.inflate(R.layout.template_spinner, null) as Spinner

        spinner.adapter =  ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.tag = index
        return spinner
    }


    @SuppressLint("MissingPermission")
    fun createAddressButton(index: Int? ,context: Context): LinearLayout{

        val layoutParams =  LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParams.setMargins(8, 8, 8, 8);
        val layout = LinearLayout(context)
        val li = LayoutInflater.from(context)
        val btn = li.inflate(R.layout.template_button_address, null) as Button
        val progressBar = ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal)

        layout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL

        btn.text = title;
        btn.layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.tag = index
        progressBar.isIndeterminate = true
        progressBar.visibility = View.GONE


        btn.setOnClickListener {
          //  mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, mLocationListener)
            progressBar.visibility = View.VISIBLE
            layout.isEnabled = false

            SingleShotLocationProvider.requestSingleUpdate(context,
                    object : SingleShotLocationProvider.LocationCallback {
                        override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates) {
                            Log.d("Location", "my location is " + location.toString())


                            val geocoder = Geocoder(context, Locale.getDefault())
                            var errorMessage = ""
                            var addresses: List<Address> = emptyList()

                            try {
                                addresses = geocoder.getFromLocation(
                                        location.latitude.toDouble(),
                                        location.longitude.toDouble(),
                                        // In this sample, we get just a single address.
                                        1)
                            } catch (ioException: IOException) {
                                // Catch network or other I/O problems.
                                errorMessage = context.getString(R.string.service_not_available)
                                Log.e(TAG, errorMessage, ioException)

                            } catch (illegalArgumentException: IllegalArgumentException) {
                                // Catch invalid latitude or longitude values.
                                errorMessage = context.getString(R.string.invalid_lat_long_used)
                                Log.e(TAG, "$errorMessage. Latitude = $location.latitude , " +
                                        "Longitude =  $location.longitude", illegalArgumentException)

                            }

                            // Handle case where no address was found.
                            if (addresses.isEmpty()) {
                                if (errorMessage.isEmpty()) {
                                    errorMessage = context.getString(R.string.no_address_found)
                                    Log.e(TAG, errorMessage)

                                }
                                //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
                            } else {
                                val address = addresses[0]

                                (layout.getChildAt(2) as TextInputLayout).editText?.setText(address.thoroughfare + " " + address.subThoroughfare)
                                (layout.getChildAt(3) as TextInputLayout).editText?.setText(address.subLocality)
                                (layout.getChildAt(4) as TextInputLayout).editText?.setText(address.adminArea) //ciudad
                                (layout.getChildAt(5) as TextInputLayout).editText?.setText(address.subAdminArea)
                                (layout.getChildAt(6) as TextInputLayout).editText?.setText(address.postalCode)




                            }

                            layout.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    })

        }


        layout.addView(btn)
        layout.addView(progressBar)
        layout.addView(createEditText(null,context,"Calle",TypeFied.TEXT),layoutParams)
        layout.addView(createEditText(null,context,"Colonia",TypeFied.TEXT),layoutParams)
        layout.addView(createEditText(null,context,"Estado",TypeFied.TEXT),layoutParams)
        layout.addView(createEditText(null,context,"Municipio",TypeFied.TEXT),layoutParams)
        layout.addView(createEditText(null,context,"Codigo Postal",TypeFied.TEXT),layoutParams)



        return layout
    }




    fun createTextviewLabel(context: Context) : TextView{
        val textView =  TextView(context)
        textView.text = title
        textView.setTextAppearance(context,R.style.TextView_InputLabel)

        return textView
    }


    fun createRadioButtonOptions(index: Int?,context: Context): RadioGroup{

        val rg = RadioGroup(context) //create the RadioGroup
        val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 16, 16, 16)
        rg.tag = index


        rg.orientation = RadioGroup.VERTICAL//or RadioGroup.VERTICAL
        for (i in 0 until options!!.size) {
            val rad = RadioButton(context)
            rad.id = i
            rad.text = options!![i]
            rad.textSize =18f
            rad.layoutParams = params
            rad.setPadding(8,8,8,8)

            rg.addView(rad)
        }

        if(rg.childCount > 0)
            rg.check(rg.getChildAt(0).id)


        /*rg.setOnCheckedChangeListener { group, checkedId ->
            Log.e("RAD-ID",checkedId.toString())
            value = options!![checkedId]
        }*/

        return rg
    }




    fun isValid():Boolean{
        return when(type){
            TypeFied.TEXT-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_EMAIL-> !(value?.get(0)!!.isNotEmpty() && required)&& isEmailValid(value?.get(0)!!)
            TypeFied.TEXT_PASSWORD-> !(value?.get(0)!!.isNotEmpty() && required) && isValidPassword(value?.get(0)!!)
            TypeFied.TEXT_NUM-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.SELECT_OPTION-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_DEC-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_PHONE-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_CURP-> !((value?.get(0)!!.isNotEmpty() && required) && isValidCURP(value?.get(0)!!))
            TypeFied.TEXT_RFC-> !((value?.get(0)!!.isNotEmpty() && required) && isValidRFC(value?.get(0)!!))
            else -> {
                return true
            }
        }

    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun isValidPassword(password:String): Boolean{
        return password.length > 8
    }

    fun isValidRFC(rfc:String, aceptarGenerico:Boolean = false):Boolean {
        val validado = if (rfc.length == 12) Pattern
                .compile("^([A-ZÑ\\x26]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])([A-Z]|[0-9]){2}([A]|[0-9]){1})?\$")
                .matcher(rfc).matches()
        else
            Pattern.compile("^^([A-ZÑ\\x26]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])([A-Z]|[0-9]){2}([A]|[0-9]){1})?\$")
                    .matcher(rfc).matches()

        if (!validado)  //Coincide con el formato general del regex?
            return false;

        //Separar el dígito verificador del resto del RFC
        val digitoVerificador = rfc[rfc.lastIndex]
        val rfcSinDigito = rfc.substring(0, rfc.length - 1)
        val len = rfcSinDigito.length

        //Obtener el digito esperado
        val diccionario = "0123456789ABCDEFGHIJKLMN&OPQRSTUVWXYZ Ñ"
        val indice = len + 1
        var suma: Int
        var digitoEsperado: String

        if (len == 12) suma = 0
        else suma = 481; //Ajuste para persona moral

        for (i in 0 until len) {
            suma += diccionario.indexOf(rfcSinDigito[i]) * (indice - i);
        }
            digitoEsperado = (11 - suma % 11).toString()
            if (digitoEsperado == 11.toString())
                digitoEsperado = "0"
            else if (digitoEsperado == 10.toString())
                digitoEsperado = "A"
            //El dígito verificador coincide con el esperado?
            // o es un RFC Genérico (ventas a público general)?
            if ((digitoVerificador.toString() != digitoEsperado) && (!aceptarGenerico || rfcSinDigito + digitoVerificador != "XAXX010101000"))
                return false
            else if (!aceptarGenerico && rfcSinDigito + digitoVerificador == "XEXX010101000")
                return false



            return true

    }


    fun isValidCURP(curp: String):Boolean {

        val validado = Pattern
                .compile("^[a-zA-Z]{4}((\\d{2}((0[13578]|1[02])(0[1-9]|[12]\\d|3[01])|(0[13456789]|1[012])(0[1-9]|[12]\\d|30)|02(0[1-9]|1\\d|2[0-8])))|([02468][048]|[13579][26])0229)(H|M)(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|SM|NE)([a-zA-Z]{3})([a-zA-Z0-9\\s]{1})\\d{1}\$+")
                .matcher(curp).matches()

        return if (!validado)
            false
        else{
            if(curp.length == 18){
                validado && validDigitiCURP(curp)
            }else{
                validado
            }

        }

    }

    private fun validDigitiCURP(curp: String):Boolean{
        val VALORES = "0123456789ABCDEFGHIJKLMN\u00D1OPQRSTUVWXYZ";
        if (curp.length != 18) {
            return false
        }

        var resultado = 0
        for (i in 0..16) {
            val valor = VALORES.indexOf(curp[i])
            resultado += valor * (18 - i)
        }
        resultado = 10 - resultado % 10
        val digito: String
        digito = if (resultado >= 10) {
            "0"
        } else {
            resultado.toString()
        }

        return curp.endsWith(digito)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeValue(upperCase)
        parcel.writeByte(if (required) 1 else 0)
        parcel.writeString(errorMessage)
        parcel.writeValue(maxLength)
        parcel.writeValue(minDate)
        parcel.writeValue(maxDate)
        parcel.writeString(this.type.name)
        parcel.writeList(value)
        parcel.writeList(options)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Field> {
        override fun createFromParcel(parcel: Parcel): Field {
            return Field(parcel)
        }

        override fun newArray(size: Int): Array<Field?> {
            return arrayOfNulls(size)
        }
    }


}