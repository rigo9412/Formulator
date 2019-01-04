package com.rigo.ramos.formslibrary.views


import android.app.TimePickerDialog
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import kotlinx.android.synthetic.main.notification_template_part_time.*

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import android.widget.NumberPicker
import android.content.DialogInterface





/**
 * Workaround for this bug: https://code.google.com/p/android/issues/detail?id=222208
 * In Android 7.0 Nougat, spinner mode for the TimePicker in TimePickerDialog is
 * incorrectly displayed as clock, even when the theme specifies otherwise, such as:
 *
 * <resources>
 * <style name="Theme.MyApp" parent="Theme.AppCompat.Light.NoActionBar">
<item name="android:timePickerStyle">@style/Widget.MyApp.TimePicker</item>
</style>
 *
 * <style name="Widget.MyApp.TimePicker" parent="android:Widget.Material.TimePicker">
<item name="android:timePickerMode">spinner</item>
</style>
</resources> *
 *
 * May also pass TimePickerDialog.THEME_HOLO_LIGHT as an argument to the constructor,
 * as this theme has the TimePickerMode set to spinner.
 */
class TimePickerDialogFixedNougatSpinner : TimePickerDialog {

    val TIME_PICKER_INTERVAL = 15
    private var callback: TimePickerDialog.OnTimeSetListener? = null
    private var mIgnoreEvent = false
    private var timePicker: TimePicker? = null
    private var lastHour = -1
    private var lastMinute = -1
    /**
     * Creates a new time picker dialog.
     *
     * @param context the parent context
     * @param listener the listener to call when the time is set
     * @param hourOfDay the initial hour
     * @param minute the initial minute
     * @param is24HourView whether this is a 24 hour view or AM/PM
     */
    constructor(
        context: Context,
        listener: TimePickerDialog.OnTimeSetListener,
        hourOfDay: Int,
        minute: Int,
        is24HourView: Boolean
    ) : super(context, listener, hourOfDay, minute, is24HourView) {
        lastHour = hourOfDay;
        lastMinute = minute;
        fixSpinner(context, hourOfDay, minute, is24HourView)
        this.callback = listener;
    }

    /**
     * Creates a new time picker dialog with the specified theme.
     *
     * @param context the parent context
     * @param themeResId the resource ID of the theme to apply to this dialog
     * @param listener the listener to call when the time is set
     * @param hourOfDay the initial hour
     * @param minute the initial minute
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    constructor(
        context: Context,
        themeResId: Int,
        listener: TimePickerDialog.OnTimeSetListener,
        hourOfDay: Int,
        minute: Int,
        is24HourView: Boolean
    ) : super(context, themeResId, listener, hourOfDay, minute, is24HourView) {
        lastHour = hourOfDay;
        lastMinute = minute;
        fixSpinner(context, hourOfDay, minute, is24HourView)
        this.callback = listener;

    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (callback != null && timePicker != null) {
            timePicker?.clearFocus()
            callback?.onTimeSet(
                timePicker, timePicker?.getCurrentHour()!!,
                timePicker?.getCurrentMinute()!! * TIME_PICKER_INTERVAL
            )
        }
    }

    private fun fixSpinner(context: Context, hourOfDay: Int, minute: Int, is24HourView: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // android:timePickerMode spinner and clock began in Lollipop
            try {
                // Get the theme's android:timePickerMode
                val MODE_SPINNER = 1
                val styleableClass = Class.forName("com.android.internal.R\$styleable")
                val timePickerStyleableField = styleableClass.getField("TimePicker")
                val timePickerStyleable = timePickerStyleableField.get(null) as IntArray
                val a = context.obtainStyledAttributes(null, timePickerStyleable, android.R.attr.timePickerStyle, 0)
                val timePickerModeStyleableField = styleableClass.getField("TimePicker_timePickerMode")
                val timePickerModeStyleable = timePickerModeStyleableField.getInt(null)
                val mode = a.getInt(timePickerModeStyleable, MODE_SPINNER)
                a.recycle()
                if (mode == MODE_SPINNER) {
                    val timePicker = findField(
                        TimePickerDialog::class.java,
                        TimePicker::class.java,
                        "mTimePicker"
                    )!!.get(this) as TimePicker
                    val delegateClass = Class.forName("android.widget.TimePicker\$TimePickerDelegate")
                    val delegateField = findField(TimePicker::class.java, delegateClass, "mDelegate")
                    var delegate = delegateField!!.get(timePicker)
                    val spinnerDelegateClass: Class<*>
                    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
                        spinnerDelegateClass = Class.forName("android.widget.TimePickerSpinnerDelegate")
                    } else {
                        // TimePickerSpinnerDelegate was initially misnamed TimePickerClockDelegate in API 21!
                        spinnerDelegateClass = Class.forName("android.widget.TimePickerClockDelegate")
                    }
                    // In 7.0 Nougat for some reason the timePickerMode is ignored and the delegate is TimePickerClockDelegate
                    if (delegate.javaClass != spinnerDelegateClass) {
                        delegateField.set(timePicker, null) // throw out the TimePickerClockDelegate!
                        timePicker.removeAllViews() // remove the TimePickerClockDelegate views
                        val spinnerDelegateConstructor = spinnerDelegateClass.getConstructor(
                            TimePicker::class.java,
                            Context::class.java,
                            AttributeSet::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        )
                        spinnerDelegateConstructor.isAccessible = true
                        // Instantiate a TimePickerSpinnerDelegate
                        delegate = spinnerDelegateConstructor.newInstance(
                            timePicker,
                            context,
                            null,
                            android.R.attr.timePickerStyle,
                            0
                        )
                        delegateField.set(timePicker, delegate) // set the TimePicker.mDelegate to the spinner delegate
                        // Set up the TimePicker again, with the TimePickerSpinnerDelegate
                        timePicker.setIs24HourView(is24HourView)
                        timePicker.currentHour = hourOfDay
                        timePicker.currentMinute = minute
                        timePicker.setOnTimeChangedListener(this)
                    }
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
    }

    private fun findField(objectClass: Class<*>, fieldClass: Class<*>, expectedName: String): Field? {
        try {
            val field = objectClass.getDeclaredField(expectedName)
            field.isAccessible = true
            return field
        } catch (e: NoSuchFieldException) {
        }
        // ignore
        // search for it if it wasn't found under the expected ivar name
        for (searchField in objectClass.declaredFields) {
            if (searchField.type == fieldClass) {
                searchField.isAccessible = true
                return searchField
            }
        }
        return null
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            val classForid = Class.forName("com.android.internal.R\$id")
            val timePickerField = classForid.getField("timePicker")
            this.timePicker = findViewById<View>(timePickerField.getInt(null)) as TimePicker
            val field = classForid.getField("minute")

            val mMinuteSpinner = timePicker?.findViewById(field.getInt(null)) as NumberPicker
            mMinuteSpinner.minValue = 0
            mMinuteSpinner.maxValue = 60 / TIME_PICKER_INTERVAL - 1
            val displayedValues = arrayListOf<String>()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            mMinuteSpinner.displayedValues = displayedValues.toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)
        if (lastHour !== hourOfDay && lastMinute !== minute) {
            view.currentHour = lastHour
            lastMinute = minute
        } else {
            lastHour = hourOfDay
            lastMinute = minute
        }
    }
}
