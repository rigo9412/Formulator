package com.rigo.ramos.formslibrary.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputLayout
import com.rigo.ramos.formslibrary.R
import com.rigo.ramos.formslibrary.views.SingleShotLocationProvider
import java.io.IOException
import java.util.*

class FieldAddress(): FieldText(){


    constructor(parcel: Parcel) : this() {
        id  = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
        title = parcel.readString()!!
        upperCase = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        required = parcel.readByte() != 0.toByte()
        errorMessage = parcel.readString()
        maxLength = parcel.readValue(Int::class.java.classLoader) as? Int
        minDate = parcel.readValue(Long::class.java.classLoader) as? Long
        maxDate = parcel.readValue(Long::class.java.classLoader) as? Long
        type = TypeFied.valueOf(parcel.readString()!!)
        value = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?
    }


    constructor(id:ArrayList<String>,title:String,required: Boolean,type:TypeFied,value:ArrayList<String>? = arrayListOf()): this(){
        this.id = id
        this.title = title
        this.required = required
        this.value = value
        this.type = type
    }

    @SuppressLint("MissingPermission")
    fun createAddressButton(index: Int? ,context: Context): LinearLayout {

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
                            Log.e(ContentValues.TAG, errorMessage, ioException)

                        } catch (illegalArgumentException: IllegalArgumentException) {
                            // Catch invalid latitude or longitude values.
                            errorMessage = context.getString(R.string.invalid_lat_long_used)
                            Log.e(
                                ContentValues.TAG, "$errorMessage. Latitude = $location.latitude , " +
                                        "Longitude =  $location.longitude", illegalArgumentException)

                        }

                        // Handle case where no address was found.
                        if (addresses.isEmpty()) {
                            if (errorMessage.isEmpty()) {
                                errorMessage = context.getString(R.string.no_address_found)
                                Log.e(ContentValues.TAG, errorMessage)

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

    companion object CREATOR : Parcelable.Creator<FieldAddress> {
        override fun createFromParcel(parcel: Parcel): FieldAddress {
            return FieldAddress(parcel)
        }

        override fun newArray(size: Int): Array<FieldAddress?> {
            return arrayOfNulls(size)
        }
    }


}