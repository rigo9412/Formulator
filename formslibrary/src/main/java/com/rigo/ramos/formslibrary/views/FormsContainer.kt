package com.rigo.ramos.formslibrary.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.rigo.ramos.formslibrary.R
import com.rigo.ramos.formslibrary.model.ActionsListenerForm
import com.rigo.ramos.formslibrary.model.Form
import com.rigo.ramos.formslibrary.model.SharedPreference
import kotlinx.android.synthetic.main.container_forms.*
import org.json.JSONObject








/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

class FormsContainer : DialogFragment(), FormFragment.OnInteractionFormListner {

    lateinit var listener: ActionsListenerForm
    private var forms =  ArrayList<Form>()
    private lateinit var adapter:BottomAdapter
    private var currentForm = -1;
    @SuppressLint("RestrictedApi")
    override fun lastField() {
        hideControls()
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = arguments
        if(extras != null || extras!!.containsKey(EXTRA_FORMS)) {
            setStyle(DialogFragment.STYLE_NORMAL, extras.getInt(EXTRA_THEME,R.style.FullScreenDialogStyle));
        }else{
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view  = inflater.inflate(R.layout.container_forms, container, false);
        adapter = BottomAdapter(this.childFragmentManager)

        return view;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       val extras = arguments
        val pref = SharedPreference(this.context!!)
        if(extras != null || extras!!.containsKey(EXTRA_FORMS)) {
            addForms(extras!!.getParcelableArrayList(EXTRA_FORMS)!!)

            //if(extras.containsKey(EXTRA_THEME))
            //this.activity?.setTheme(extras.getInt(EXTRA_THEME,0))

            /*if(extras.containsKey(EXTRA_BACKGROUND_COLOR)){
                pref.setColor(SharedPreference.COLOR_BACKGROUND,extras.getInt(EXTRA_BACKGROUND_COLOR,0))
                this.fullscreen_content.setBackgroundColor(resources.getColor(extras.getInt(EXTRA_BACKGROUND_COLOR,0)))
            }*/

            if(extras.containsKey(EXTRA_TEXT_COLOR)){
                pref.setColor(SharedPreference.COLOR_TEXT,extras.getInt(EXTRA_TEXT_COLOR,0))
            }

            if(extras.containsKey(EXTRA_ACCENT_COLOR)){
                pref.setColor(SharedPreference.COLOR_ACCENT,extras.getInt(EXTRA_ACCENT_COLOR,0))
            }

            if(extras.containsKey(EXTRA_PRIMARY_COLOR)){
                pref.setColor(SharedPreference.COLOR_PRIMARY,extras.getInt(EXTRA_PRIMARY_COLOR,0))
            }


            btnActionSave.setTextColor(resources.getColor(pref.getColor(SharedPreference.COLOR_ACCENT)!!))
            tvSteps.setTextColor(resources.getColor(pref.getColor(SharedPreference.COLOR_TEXT)!!))
            btnActionSave.setTextColor(resources.getColor(pref.getColor(SharedPreference.COLOR_TEXT)!!))
            btnBack.setColorFilter(ContextCompat.getColor(this.context!!, pref.getColor(SharedPreference.COLOR_TEXT)!!), android.graphics.PorterDuff.Mode.SRC_IN)
            btnNext.setBackgroundColor(pref.getColor(SharedPreference.COLOR_ACCENT)!!)


        }else{
            this.dismiss()
        }


        btnActionSave.setOnClickListener {
            if(isValidForm()) {
                val result = JSONObject()
                forms.forEach { form ->

                    val resulAux = form.generateJsonResult()
                    for( i in 0 until resulAux.length()) {
                        result.put(resulAux.names()[i].toString(),resulAux.get(resulAux.names()[i].toString()))
                    }


                }
                Log.e("JSON-RESULT",result.toString())
                listener.onSave(result)
            }
        }

        btnNext.setOnClickListener {
            if(currentForm < adapter.count) {
                if (isValidForm()) {
                    currentForm++
                    tvSteps.text = "${(currentForm + 1)} de ${adapter.count}"
                    Log.e("CURRENT-FORM", currentForm.toString())
                    viewPager.currentItem = currentForm

                }
            }

            hideControls()
        }



        btnBack.setOnClickListener {
            if(currentForm == 0)
                this@FormsContainer.dismiss()
            else{
                currentForm--
                tvSteps.text =  "${(currentForm + 1)} de ${adapter.count}"
                Log.e("CURRENT-FORM",currentForm.toString())
                viewPager.currentItem = currentForm
            }

            hideControls()
        }

    }



    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as ActionsListenerForm
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = activity as ActionsListenerForm
    }



    fun isValidForm():Boolean{
        val fragment = adapter.getItem(currentForm) as FormFragment
        val result =fragment.validateForm()


        return if(result != null){
            this.forms[currentForm].fields = result.fields
            true
        }else
            false


    }


    fun addForms(forms: ArrayList<Form>) {

        this.forms = forms
        forms.forEach {
            adapter.addFragment(FormFragment.newInstance(it))
        }

        viewPager.setPagingEnabled(false)
        viewPager.adapter = adapter

        currentForm = 0
        tvSteps.text =  "${(currentForm + 1)} de ${adapter.count}"

        hideControls()
    }




    @SuppressLint("RestrictedApi")
    fun hideControls(){
        if(currentForm == adapter.count-1) {
            btnActionSave.visibility = View.VISIBLE
            btnNext.visibility = View.INVISIBLE
        }else{
            btnActionSave.visibility = View.INVISIBLE
            btnNext.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapter.getItem(currentForm).onActivityResult(requestCode,resultCode,data)
    }


    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300

        val EXTRA_FORMS = "forms"
        val EXTRA_THEME = "theme"
        val EXTRA_TEXT_COLOR = "text"
        val EXTRA_ACCENT_COLOR = "accent"
        val EXTRA_PRIMARY_COLOR = "primary"
        val EXTRA_BACKGROUND_COLOR = "backgroud"
        val TAG = "FORM-CONTAINER"
        val BROADCAST_PRIVIDER_FORM = "BROADCAST_PRIVIDER_FORM"
        val BROADCAST_PRIVIDER_FORM_OK = "BROADCAST_PRIVIDER_FORM_OK"
        val BROADCAST_PRIVIDER_FORM_MESSAGE = "BROADCAST_PRIVIDER_FORM_MESSAGE"
    }
}
