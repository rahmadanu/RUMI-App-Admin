package com.example.rumiappadmin.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rumiappadmin.models.Menu
import com.example.rumiappadmin.utils.Constants
import com.example.rumiappadmin.R
import com.example.rumiappadmin.databinding.ActivityAddMenuItemBinding
import com.example.rumiappadmin.firestore.FirestoreClass
import com.example.rumiappadmin.utils.GlideLoader
import java.io.IOException

class AddMenuItemActivity : BaseActivity(), View.OnClickListener {

    private var _binding: ActivityAddMenuItemBinding? = null
    private val binding get() = _binding!!

    private var mSelectedImageUri: Uri? = null

    private var mMenuItemImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.ivAddUpdateItem.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setupActionBar() {
        val toolbar = binding.toolbarAddItemActivity
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_update_item -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddMenuItemActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        uploadItemImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    binding.ivAddUpdateItem.setImageResource(R.drawable.ic_vector_add_photo)

                    try {
                        mSelectedImageUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageUri!!, binding.ivItemImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, resources.getString(R.string.image_selection_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Result Canceled", "Image selection canceled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            mSelectedImageUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_menu_item_image), true)
                false
            }

            TextUtils.isEmpty(binding.etItemTitle.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_menu_item_title), true)
                false
            }

            TextUtils.isEmpty(binding.etItemDescription.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_menu_item_description), true)
                false
            }

            TextUtils.isEmpty(binding.etItemPrice.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_menu_item_price), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadItemImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageUri, Constants.MENU_ITEM_IMAGE)
    }

    fun menuItemUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(this@AddMenuItemActivity, resources.getString(R.string.menu_item_uploaded_success_message), Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@AddMenuItemActivity, MenuActivity::class.java))

        finish()
    }

    fun imageUploadSuccess(imageUrl: String) {
        mMenuItemImageURL = imageUrl

        uploadMenuItemDetails()
    }

    private fun uploadMenuItemDetails() {
        val username = this.getSharedPreferences(
            Constants.RUMI_PREFERENCES,
            Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val menuItem = Menu(
            FirestoreClass().getCurrentUserId(),
            username,
            binding.etItemTitle.text.toString().trim(),
            binding.etItemPrice.text.toString().trim(),
            binding.etItemDescription.text.toString().trim(),
            mMenuItemImageURL
        )

        FirestoreClass().uploadMenuItemDetails(this, menuItem)
    }
}