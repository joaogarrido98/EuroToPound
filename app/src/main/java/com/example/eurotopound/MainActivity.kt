package com.example.eurotopound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "GBP"
    var convertedToCurrency = "USD"
    var conversionRate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        textChanged()

    }

    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.fromSpinner)
        val secondSpinner: Spinner = findViewById(R.id.toSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.currenciesSecond,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            secondSpinner.adapter = adapter
        }
        spinner.onItemSelectedListener = (object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getRates()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
        secondSpinner.onItemSelectedListener = (object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getRates()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getRates() {
        if (from != null && from.text.isNotEmpty() && from.text.isNotBlank()) {
            if (baseCurrency != convertedToCurrency) {
                if (baseCurrency == "EUR"){
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val api = "https://api.exchangeratesapi.io/latest"
                            val result = URL(api).readText()
                            val jsonObject = JSONObject(result)
                            conversionRate = jsonObject.getJSONObject("rates").getString(convertedToCurrency).toFloat()
                            Log.d("Main", conversionRate.toString())
                            Log.d("Main", result)

                            withContext(Dispatchers.Main) {
                                val text =
                                    ((from.text.toString().toFloat()) * conversionRate).toString()
                                to?.setText(text)
                            }
                        } catch (e: Exception) {
                            Log.d("Main", e.toString())
                        }
                    }

                }else{
                    val api = "https://api.exchangeratesapi.io/latest?symbols=$baseCurrency,$convertedToCurrency"
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val result = URL(api).readText()
                            val jsonObject = JSONObject(result)
                            conversionRate =
                                jsonObject.getJSONObject("rates").getString(convertedToCurrency)
                                    .toFloat()
                            Log.d("Main", conversionRate.toString())
                            Log.d("Main", result)

                            withContext(Dispatchers.Main) {
                                val text =
                                    ((from.text.toString().toFloat()) * conversionRate).toString()
                                to?.setText(text)
                            }
                        } catch (e: Exception) {
                            Log.d("Main", e.toString())
                        }
                    }
                }

            } else {
                Toast.makeText(applicationContext, "These are the same currency", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun textChanged() {
        from.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "On Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "On Text Changed")
            }

            override fun afterTextChanged(s: Editable?) {
              try {
                  getRates()
              }catch (e:Exception){
                  Log.d("Main", e.toString())
              }
            }

        })
    }
}