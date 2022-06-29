package com.example.rumiappadmin.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.rumiappadmin.models.Menu
import com.example.rumiappadmin.utils.Constants
import com.example.rumiappadmin.models.Admin
import com.example.rumiappadmin.models.Order
import com.example.rumiappadmin.ui.activities.AddMenuItemActivity
import com.example.rumiappadmin.ui.activities.LoginActivity
import com.example.rumiappadmin.ui.activities.MenuItemDetailsActivity
import com.example.rumiappadmin.ui.fragments.MenuFragment
import com.example.rumiappadmin.ui.fragments.OrdersFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFirestoreClass = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFirestoreClass.collection(Constants.ADMIN)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(Admin::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(Constants.RUMI_PREFERENCES, Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.restaurantName}")
                editor.apply()

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    /*is SettingsProfileActivity -> {
                        activity.userDetailsSuccess(user)
                    }*/
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    /*is SettingsProfileActivity -> {
                        activity.hideProgressDialog()
                    }*/
                }

                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }
    }

    /*fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestoreClass.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }

            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user details")
            }
    }*/

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileUri)
        )

        storageRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.i("Firebase image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        when (activity) {
                            is AddMenuItemActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is AddMenuItemActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }
    }

    fun uploadMenuItemDetails(activity: AddMenuItemActivity, menuInfo: Menu) {

        mFirestoreClass.collection(Constants.MENU)
            .document()
            .set(menuInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.menuItemUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while uploading the menu item details.", e)
            }
    }

    fun getMenuList(fragment: MenuFragment) {
        mFirestoreClass.collection(Constants.MENU)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val menuList: ArrayList<Menu> = ArrayList()

                for (i in document.documents) {
                    val menu = i.toObject(Menu::class.java)!!
                    menu.menu_id = i.id

                    menuList.add(menu)
                }
                fragment.successGetMenuList(menuList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting menu list", e)
            }
    }

    fun deleteMenu(fragment: MenuFragment, menuId: String) {
        mFirestoreClass.collection(Constants.MENU)
            .document(menuId)
            .delete()
            .addOnSuccessListener {
                fragment.successDeleteMenuItem()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(fragment.requireActivity().javaClass.simpleName, "Error while deleting the menu item.", e)
            }
    }

    fun getMenuDetails(activity: MenuItemDetailsActivity, menuId: String) {

        mFirestoreClass.collection(Constants.MENU)
            .document(menuId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val menu = document.toObject(Menu::class.java)!!

                activity.menuDetailsSuccess(menu)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting menu details", e)
            }
    }

    fun getOrderList(fragment: OrdersFragment) {

        mFirestoreClass.collection(Constants.ORDERS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                fragment.getOrderListSuccess(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the order list", e)
            }
    }
}