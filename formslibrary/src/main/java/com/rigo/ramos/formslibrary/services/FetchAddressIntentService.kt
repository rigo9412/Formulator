package com.rigo.ramos.formslibrary.services

import android.app.IntentService
import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver

import android.util.Log
import com.rigo.ramos.formslibrary.R
import java.io.IOException
import java.security.Provider
import java.util.*

class FetchAddressIntentService : IntentService(FetchAddressIntentService::class.simpleName) {
    private var receiver: ResultReceiver? = null

     override fun onHandleIntent(intent: Intent?) {
        intent ?: return


        val geocoder = Geocoder(this, Locale.getDefault())


        var errorMessage = ""

        // Get the location passed to this service through an extra.
        val location: Location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA)

        // ...

        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, we get just a single address.
                    1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "$errorMessage. Latitude = $location.latitude , " +
                    "Longitude =  $location.longitude", illegalArgumentException)
        }

        // Handle case where no address was found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            Log.i(TAG, getString(R.string.address_found))
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    addressFragments.joinToString(separator = "\n"))
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)
    }


    public object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.rigo.ramos.formslibrary.services.FetchAddressIntentService"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }


}