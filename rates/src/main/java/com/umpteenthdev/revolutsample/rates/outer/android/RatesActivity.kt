package com.umpteenthdev.revolutsample.rates.outer.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umpteenthdev.revolutsample.rates.R

class RatesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RatesFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent = Intent(context, RatesActivity::class.java)
    }
}
