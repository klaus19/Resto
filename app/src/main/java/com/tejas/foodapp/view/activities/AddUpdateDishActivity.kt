package com.tejas.foodapp.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.tejas.foodapp.R
import com.tejas.foodapp.databinding.ActivityAddUpdateDishBinding
import com.tejas.foodapp.databinding.DialogCustomImageSelectionBinding

class AddUpdateDishActivity : AppCompatActivity(),View.OnClickListener {

    // Create a global variable for binding
    private lateinit var mBinding: ActivityAddUpdateDishBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.iv_add_dish_image ->{
                    customImageSelectionDialog()

                    return
                }
            }
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val binding:DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener{
            Toast.makeText(this,"Hello",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener{
            Toast.makeText(this,"Gallery",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }



        dialog.show()
    }
}