package com.cm.cryptogram.ui

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cm.cryptogram.adapter.HistoryAdapter
import com.cm.cryptogram.model.HistoryItem
import org.json.JSONArray


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
            val httpClient = HttpRequestHelper()
            httpClient.setTargetUrl("https://ctrytogram-default-rtdb.asia-southeast1.firebasedatabase.app/.json")
            val result = httpClient.requestKtorIo()
            jsonData = result
            Log.i("http",result);
        }

        if(!preferenceHelper.loginState){
            //로그인상태가 아닐시 로그인 화면으로 전환 시도.
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
            return
        }
        historyDatas = mutableListOf<HistoryItem>()
        recyclerView = findViewById(R.id.historyRecycler)
        recyclerView.setHasFixedSize(true)
        viewManger = LinearLayoutManager(this)
        recyclerView.layoutManager = viewManger
        viewAdapter = HistoryAdapter(this, historyDatas)
        recyclerView.adapter = viewAdapter
        viewAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.VISIBLE
        recyclerView.requestLayout()

        database = Firebase.database.reference

        var upi = preferenceHelper.userPrefInfo
        Log.i("UPI", upi.toString())
        var tempStr = "[" + upi + "]"
        Log.i("firebase", "Got value ${tempStr}")
        var jsonStr = JSONArray(tempStr)
        Log.i("firebase", "Got Json ${jsonStr.toString()}")
        var hm : HashMap<String, Any> = HashMap()
        for (key in jsonStr.getJSONObject(0).keys()) {
            hm = HashMap()
            Log.i("key", key)
            historyDatas.add(HistoryItem(key,jsonStr.getJSONObject(0).getString(key)))
        }
        if ( preferenceHelper.keyWord != null ) {
            hm = HashMap()
            hm.put(preferenceHelper.keyWord!!, "1")
            historyDatas.add(HistoryItem(preferenceHelper.keyWord!!,"1"))
            database.child(preferenceHelper.userIndex!!).child("keyword_history").updateChildren(hm)
        }

        //시작화면은 키워드화면으로
        replaceFragment(newInstance(KeywordSettingFragment()))
        historyDatas.sortByDescending{ it.content }
        viewAdapter.notifyDataSetChanged()
        recyclerView.requestLayout()

        binding.bottomNavigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.keyword -> {
                        Log.i("http","keyword pressed")
                        replaceFragment(newInstance(KeywordSettingFragment()))
                        return true
                    }
                    R.id.invest -> {
                        Log.i("http","invest pressed")
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