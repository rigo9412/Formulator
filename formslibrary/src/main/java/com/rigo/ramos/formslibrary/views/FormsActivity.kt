package com.rigo.ramos.formslibrary.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.rigo.ramos.formslibrary.R
import com.rigo.ramos.formslibrary.model.Form
import kotlinx.android.synthetic.main.activity_forms.*
import org.json.JSONObject




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
const val RESULT_FORM = "result_form"
class FormsActivity : AppCompatActivity(), FormFragment.OnInteractionFormListner {
    @SuppressLint("RestrictedApi")
    override fun lastField() {
        hideControls()
    }

    private var forms=  ArrayList<Form>()
    var adapter = BottomAdapter(this.supportFragmentManager)
    private var currentForm = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forms)


        val extras = intent.extras

        if(extras != null || extras!!.containsKey(EXTRA_FORMS)) {
            addForms(intent?.extras!!.getParcelableArrayList(EXTRA_FORMS)!!)

            if(extras.containsKey(EXTRA_THEME))
                setTheme(extras.getInt(EXTRA_THEME,0))

            if(extras.containsKey(EXTRA_BACKGROUND_COLOR)){
                val view = this.window.decorView
                view.setBackgroundColor(extras.getInt(EXTRA_BACKGROUND_COLOR,0))
            }

        }else{
            this.finish()
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

                val i = Intent()
                i.putExtra(RESULT_FORM,result.toString())
                setResult(Activity.RESULT_OK,i)
                this@FormsActivity.finish()
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
                this@FormsActivity.finish()
            else{
                currentForm--
                tvSteps.text =  "${(currentForm + 1)} de ${adapter.count}"
                Log.e("CURRENT-FORM",currentForm.toString())
                viewPager.currentItem = currentForm
            }

            hideControls()
        }


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
        val EXTRA_BACKGROUND_COLOR = "backgroud"
    }
}
