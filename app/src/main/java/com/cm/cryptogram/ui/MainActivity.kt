package com.cm.cryptogram.ui

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.cm.cryptogram.L
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.base.HttpRequestHelper
import com.cm.cryptogram.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseActivity<ActivityMainBinding>(), CoroutineScope {
    private lateinit var job: Job
    private lateinit var database: DatabaseReference
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewCreated() {
        job = Job()
        launch(Dispatchers.Main) {
            val result = HttpRequestHelper().requestKtorIo()
            jsonData = result
            L.i(result)
        }

//        database.addValueEventListener(object: ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val post = snapshot.getValue<Post>()
//                Log.d("Firebase", "Value is: " + post)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w("Firebase", "Failed to read value.", error.toException())
//            }
//
//        })

        if(!preferenceHelper.loginState){
            //로그인상태가 아닐시 로그인 화면으로 전환 시도.
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
            return
        }
        database = Firebase.database.reference

        database.child("0").child("keyword_history").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            preferenceHelper.userPrefInfo = it.value.toString()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        //시작화면은 키워드화면으로
        replaceFragment(newInstance(KeywordSettingFragment()))


        binding.bottomNavigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.keyword -> {
                        L.i("keyword pressed")
                        replaceFragment(newInstance(KeywordSettingFragment()))
                        return true
                    }
                    R.id.invest -> {
                        L.i("invest pressed")
                        replaceFragment(newInstance(InvestMentFragment()))
                        return true
                    }
                }
                return false
            }

        })
    }

    fun replaceBottomMenuIndex(index : Int){
        //바텀 네비게이션 인덱스를 전환하는 함수
      binding.bottomNavigation.menu.getItem(index)?.isChecked = true
    }

    fun replaceFragment(fragment: Fragment){
        //프래그먼트 전환을 위해 필요한 함수
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }




}