package com.tejas.foodapp.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tejas.foodapp.R
import com.tejas.foodapp.databinding.ActivityAddUpdateDishBinding

class AddUpdateDishActivity : AppCompatActivity() {

    // Create a global variable for binding
    private lateinit var mBinding: ActivityAddUpdateDishBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}