package com.cm.cryptogram.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.databinding.ActivityLoginBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private lateinit var database: DatabaseReference
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onViewCreated() {
        //로그인 버튼을 누를시
        binding.btnLogin.setOnClickListener {

            database = Firebase.database.reference

            database.child("0").child("id").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                preferenceHelper.loginInfo = it.value.toString()
                if (preferenceHelper.loginInfo.equals(binding.editId.text.toString())) {
                    preferenceHelper.loginState = true
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                else {
                    preferenceHelper.loginState = false
                    Toast.makeText(this@LoginActivity, "로그인 실패! 다시 시도하세요!", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
                preferenceHelper.loginState = false
            }

        }
    }
}