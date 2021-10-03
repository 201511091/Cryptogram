package com.cm.cryptogram.ui

import android.service.autofill.TextValueSanitizer
import android.util.Log
import android.widget.TextView
import com.cm.cryptogram.L.JSON
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.base.BaseActivity.Companion.newInstance
import com.cm.cryptogram.base.BaseFragment
import com.cm.cryptogram.databinding.FragmentKeywordBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.lang.reflect.Type

internal class KeywordSettingFragment : BaseFragment<FragmentKeywordBinding>() {

    private lateinit var database: DatabaseReference
    override fun layoutRes(): Int = R.layout.fragment_keyword

    override fun onViewCreated() {
        binding.btnSearch.setOnClickListener {
            if (binding.editSearch.text.isNullOrEmpty()) {
                return@setOnClickListener
            }

            val activity = getBaseActivity() as MainActivity?
            activity?.let {
                preferenceHelper.keyWord = binding.editSearch.text.toString()
                database = Firebase.database.reference
                var upi = preferenceHelper.userPrefInfo
                var tempStr = "["
                if (upi != null) {
                    for(i in 0..(upi.length-1)){
                        if (upi[i].equals('=')) {
                            tempStr += '"'
                            tempStr += ':'
                            tempStr += '"'
                        } else if (upi[i].equals(',')) {
                            tempStr += '"'
                            tempStr += ','
                        } else if (upi[i].equals(' ')) {
                            tempStr += ' '
                            tempStr += '"'
                        } else if (i == (upi.length - 2)) {
                            tempStr += '"'
                        } else if (i == 1) {
                            tempStr += '"'
                            tempStr += upi[i]
                        } else {
                            tempStr += upi[i]
                        }
                    }
                    tempStr += ']'
                }
                Log.i("firebase", "Got value ${tempStr}")
                var jsonStr = JSONArray(tempStr)
                Log.i("firebase", "Got Json ${jsonStr.toString()}")
                var isInHistory = false
                var hm : HashMap<String, Any> = HashMap()
//                var tvArr : ArrayList<TextView> = ArrayList()
//                tvArr.add(binding.textView)
//                tvArr.add(binding.textView2)
//                tvArr.add(binding.textView3)
//                tvArr.add(binding.textView4)
//                tvArr.add(binding.textView5)
//                tvArr.add(binding.textView6)
//                tvArr.add(binding.textView7)
//                var tvArrCnt = 0
                for (key in jsonStr.getJSONObject(0).keys()) {
                    hm = HashMap()
                    if (key.equals(preferenceHelper.keyWord)) {
                        isInHistory = true
                        var valStr = ((jsonStr.getJSONObject(0).getString(key)).toInt() + 1).toString()
                        hm.put(key, valStr)
                        database.child("0").child("keyword_history").child(key).removeValue()
                        database.child("0").child("keyword_history").updateChildren(hm)
//                        if(tvArrCnt < 5)
//                            tvArr[tvArrCnt].text = key + "을 " + valStr + "번 검색하셨어요."
                    }
//                    tvArrCnt++
                }
                if ( !isInHistory ) {
                    hm = HashMap()
                    hm.put(preferenceHelper.keyWord!!, "1")
                    database.child("0").child("keyword_history").updateChildren(hm)
//                    tvArr[tvArrCnt].text = preferenceHelper.keyWord + "을 1번 검색하셨어요."
                }

                it.replaceFragment(newInstance(InvestMentFragment()))
                it.replaceBottomMenuIndex(1)
            }
        }
    }
}