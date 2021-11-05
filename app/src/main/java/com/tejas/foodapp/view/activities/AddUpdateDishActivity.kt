package com.tejas.foodapp.view.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.tejas.foodapp.R
import com.tejas.foodapp.databinding.ActivityAddUpdateDishBinding
import com.tejas.foodapp.databinding.DialogCustomImageSelectionBinding
import com.tejas.foodapp.databinding.DialogCustomListBinding
import com.tejas.foodapp.utils.Constants
import com.tejas.foodapp.utils.Constants.DISH_TYPE
import com.tejas.foodapp.utils.Constants.dishTypes
import com.tejas.foodapp.view.adapters.CustomListItemAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    // A global variable for stored image path.
    private var mImagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this@AddUpdateDishActivity)

        // TODO Step 10: Assign the click events to the EditText fields as you have noticed while designing we have disabled few fields like Type, Category and Cooking time.
        // START
        mBinding.etType.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.etCategory.setOnClickListener(this@AddUpdateDishActivity)
        mBinding.etCookingTime.setOnClickListener(this@AddUpdateDishActivity)
        // END
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.iv_add_dish_image -> {

                customImageSelectionDialog()
                return
            }

            // TODO Step 11: Perform the action of the view and launch the dialog.
            // START
            R.id.et_type -> {
                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_type),
                    Constants.dishTypes(),
                    Constants.DISH_TYPE
                )
                return
            }

            R.id.et_category -> {
                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_category),
                    Constants.dishCategories(),
                    Constants.DISH_CATEGORY
                )
                return
            }

            R.id.et_cooking_time -> {

                customItemsListDialog(
                    resources.getString(R.string.title_select_dish_cooking_time),
                    Constants.dishCookTime(),
                    Constants.DISH_COOKING_TIME
                )
                return
            }
            // END
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {

                data?.extras?.let {
                    val thumbnail: Bitmap =
                        data.extras!!.get("data") as Bitmap // Bitmap from camera

                    // Set Capture Image bitmap to the imageView using Glide
                    Glide.with(this@AddUpdateDishActivity)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.i("ImagePath", mImagePath)

                    // Replace the add icon with edit icon once the image is loaded.
                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            } else if (requestCode == GALLERY) {

                data?.let {
                    // Here we will get the select image URI.
                    val selectedPhotoUri = data.data

                    // Set Selected Image URI to the imageView using Glide
                    Glide.with(this@AddUpdateDishActivity)
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                @Nullable e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // log exception
                                Log.e("TAG", "Error loading image", e)
                                return false // important to return false so the error placeholder can be placed
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {

                                val bitmap: Bitmap = resource.toBitmap()

                                mImagePath = saveImageToInternalStorage(bitmap)
                                Log.i("ImagePath", mImagePath)
                                return false
                            }
                        })
                        .into(mBinding.ivDishImage)

                    // Replace the add icon with edit icon once the image is selected.
                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function for ActionBar setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * A function to launch the custom image selection dialog.
     */
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        report?.let {
                            // Here after all the permission are granted launch the CAMERA to capture an image.
                            if (report.areAllPermissionsGranted()) {

                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {

            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : PermissionListener {

                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                        // Here after all the permission are granted launch the gallery to select and image.
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            "You have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * A function to save a copy of an image to internal storage for FavDishApp to use.
     *
     * @param bitmap
     */
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Mention a file name to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image absolute path
        return file.absolutePath
    }

    // TODO Step 9: Create a function to launch the Custom List dialog and pass the required params in it.
    // START
    /**
     * A function to launch the custom list dialog.
     *
     * @param title - Define the title at runtime according to the list items.
     * @param itemsList - List of items to be selected.
     * @param selection - By passing this param you can identify the list item selection.
     */
    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String) {
        val customListDialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        customListDialog.setContentView(binding.root)

        binding.tvTitle.text = title

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(this@AddUpdateDishActivity)
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(this@AddUpdateDishActivity, itemsList, selection)
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        customListDialog.show()
    }
    // END


    companion object {
        private const val CAMERA = 1

        private const val GALLERY = 2

        private const val IMAGE_DIRECTORY = "Food App"
    }}