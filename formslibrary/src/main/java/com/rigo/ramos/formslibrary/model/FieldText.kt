package com.rigo.ramos.formslibrary.model

import android.app.DatePickerDialog
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rigo.ramos.formslibrary.R
import com.rigo.ramos.formslibrary.views.FixedHoloDatePickerDialog
import java.util.*
import java.util.regex.Pattern

open class FieldText() : Field(){

    var maxLength: Int? = null
    var minDate: Long? = null
    var maxDate: Long? = null


    constructor(parcel: Parcel) : this() {
        id = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        title = parcel.readString()!!
        upperCase = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        required = parcel.readByte() != 0.toByte()
        errorMessage = parcel.readString()
        maxLength = parcel.readValue(Int::class.java.classLoader) as? Int
        minDate = parcel.readValue(Long::class.java.classLoader) as? Long
        maxDate = parcel.readValue(Long::class.java.classLoader) as? Long
        type = TypeFied.valueOf(parcel.readString()!!)
        value = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        //options = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
    }


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


    fun createEditText(index: Int?, context: Context, title:String, type:TypeFied, maxDate: Long? = null, minDate: Long? = null, maxLength: Int? = null): TextInputLayout {


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
                editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(18))

            }
            TypeFied.TEXT_RFC->{
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps(), InputFilter.LengthFilter(13))
            }

        }


        editText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        layout.addView(editText)
        return layout
    }


    override fun createView(context:Context, index: Int) : View? {
        return when(type){
            TypeFied.TEXT-> createEditText(index,context,title!!,type,null,null,maxLength)
            TypeFied.TEXT_RFC-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_CURP-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_EMAIL-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_PASSWORD-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_NUM-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_DEC-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_PHONE-> createEditText(index,context,title!!,type)
            TypeFied.TEXT_DATE-> createEditText(index,context,title!!,type,minDate,maxDate)
            else -> {
                return null
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
        parcel.writeList(id)
        parcel.writeString(title)
        parcel.writeValue(upperCase)
        parcel.writeByte(if (required) 1 else 0)
        parcel.writeString(errorMessage)
        parcel.writeValue(maxLength)
        parcel.writeValue(minDate)
        parcel.writeValue(maxDate)
        parcel.writeString(this.type.name)
        parcel.writeList(value)
    }

    override fun isValid():Boolean{
        return when(type){
            TypeFied.TEXT-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_EMAIL-> !(value?.get(0)!!.isNotEmpty() && required)&& isEmailValid(value?.get(0)!!)
            TypeFied.TEXT_PASSWORD-> !(value?.get(0)!!.isNotEmpty() && required) && isValidPassword(value?.get(0)!!)
            TypeFied.TEXT_NUM-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_DEC-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_PHONE-> !(value?.get(0)!!.isNotEmpty() && required)
            TypeFied.TEXT_CURP-> !((value?.get(0)!!.isNotEmpty() && required) && isValidCURP(value?.get(0)!!))
            TypeFied.TEXT_RFC-> !((value?.get(0)!!.isNotEmpty() && required) && isValidRFC(value?.get(0)!!))
            else -> {
                return true
            }
        }
    }


    companion object CREATOR : Parcelable.Creator<FieldText> {
        override fun createFromParcel(parcel: Parcel): FieldText {
            return FieldText(parcel)
        }

        override fun newArray(size: Int): Array<FieldText?> {
            return arrayOfNulls(size)
        }
    }

}