package com.kseyko.sharedprefdatastore

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kseyko.sharedprefdatastore.databinding.ActivityMainBinding
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.asLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Create the dataStore and give it a name same as shared preferences
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var storePreference: StorePreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Storing data into SharedPreferences
        val sharedPref = getSharedPreferences("ExampleShared", MODE_PRIVATE)

        //Alternatively, if you need just one shared preference file for your activity,
        val sharedWithPreferences = getPreferences(MODE_PRIVATE)

        // Although you can define your own key generation parameter specification
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val encryptedSharedPreferences = EncryptedSharedPreferences.create(
            "encryptedShared",
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Creating an Editor object to edit(write to the file)
        val sharedPrefEdit = sharedPref.edit()
        val sharedPrefwithPrefrencesEdit = sharedWithPreferences.edit()
        val encryptedSharedPreferencesEdit= encryptedSharedPreferences.edit()

        dataStoreImp()

        if (!sharedPref.getString("username","").isNullOrEmpty() && !sharedPref.getString("password","").isNullOrEmpty()){
            binding.edtUsername.setText(sharedPref.getString("username",""))
            binding.edtPassword.setText(sharedPref.getString("password",""))
            binding.layoutEncrypted.visibility= View.VISIBLE
            binding.txtEncryptedPassword.text =
                encryptedSharedPreferences.getString("encryptedPassword","")
        }
        if (!sharedWithPreferences.getString("usernamepref","").isNullOrEmpty()){
            binding.edtUsernamePref.setText(sharedPref.getString("usernamepref",""))
            binding.edtPasswordPref.setText(sharedPref.getString("passwordpref",""))
        }


        binding.btnLogin.setOnClickListener {
            // Storing the key and its value as the data fetched from edittext
            GlobalScope.launch {
                storePreference.storeUser(binding.edtUsernamePref.text.toString(), binding.edtPasswordPref.text.toString())
            }
            if (binding.cbRememberPref.isChecked){
                sharedPrefwithPrefrencesEdit.putString("usernamepref", binding.edtUsernamePref.text.toString())
                sharedPrefwithPrefrencesEdit.putString("passwordpref", binding.edtPasswordPref.text.toString())

                sharedPrefwithPrefrencesEdit.apply()

            }
            if(binding.cbRemember.isChecked){
                sharedPrefEdit.putString("username", binding.edtUsername.text.toString())
                sharedPrefEdit.putString("password", binding.edtPassword.text.toString())
                encryptedSharedPreferencesEdit.putString("encryptedPassword",binding.edtPassword.text.toString())
                encryptedSharedPreferencesEdit.apply()
                sharedPrefEdit.apply()
            }

            hideKeyboard()
            showSnackBar()
        }


        // Storing the key and its value as the data fetched from edittext

    }

    private fun dataStoreImp() {
        storePreference = StorePreference(this)

        // this function retrieves the saved data
        // as soon as they are stored and even
        // after app is closed and started again
        observeData()
    }
    private fun observeData() {
        // Updates age
        // every time user age changes it will be observed by userAgeFlow
        // here it refers to the value returned from the userAgeFlow function
        // of UserManager class
        this.storePreference.usernameFlow.asLiveData().observe(this) {
            binding.txtStoreUsername.text = it.toString()
        }

        // Updates name
        // every time user name changes it will be observed by userNameFlow
        // here it refers to the value returned from the usernameFlow function
        // of UserManager class
        this.storePreference.passwordFlow.asLiveData().observe(this) {
            binding.txtStorePassword.text = it.toString()
        }
    }

    fun showSnackBar(){
        Snackbar.make(
            findViewById(R.id.content),
            "Login is successful",
            Snackbar.LENGTH_LONG,

            ).setBackgroundTint(Color.GREEN).show()
    }
    fun hideKeyboard() {
        val inputManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val v = currentFocus ?: return
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}