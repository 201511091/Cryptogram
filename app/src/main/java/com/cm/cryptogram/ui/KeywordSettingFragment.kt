package com.cm.cryptogram.ui

import android.service.autofill.TextValueSanitizer
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.base.BaseActivity.Companion.historyDatas
import com.cm.cryptogram.base.BaseActivity.Companion.newInstance
import com.cm.cryptogram.base.BaseActivity.Companion.recyclerView
import com.cm.cryptogram.base.BaseActivity.Companion.viewAdapter
import com.cm.cryptogram.base.BaseFragment
import com.cm.cryptogram.databinding.FragmentKeywordBinding
import com.cm.cryptogram.model.HistoryItem
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
                var tempStr = "[" + upi + "]"
                Log.i("firebase", "Got value ${tempStr}")
                var jsonStr = JSONArray(tempStr)
                Log.i("firebase", "Got Json ${jsonStr.toString()}")
                var isInHistory = false
                var hm : HashMap<String, Any> = HashMap()
                for (key in jsonStr.getJSONObject(0).keys()) {
                    historyDatas.add(HistoryItem(key, jsonStr.getJSONObject(0).getString(key)))
                    hm = HashMap()
                    if (key.equals(preferenceHelper.keyWord)) {
                        isInHistory = true
                        var valStr = ((jsonStr.getJSONObject(0).getString(key)).toInt() + 1).toString()
                        hm.put(key, valStr)

                        database.child(preferenceHelper.userIndex!!).child("keyword_history").child(key).removeValue()
                        database.child(preferenceHelper.userIndex!!).child("keyword_history").updateChildren(hm)

                        for ( item in historyDatas ) {
                            if ( item.title.equals(key) ) {
                                val tempVal = ((item.content).toInt() + 1).toString()
                                historyDatas.remove(item)
                                historyDatas.add(HistoryItem(key, tempVal))
                                break
                            }
                        }
                    }
                }
                if ( !isInHistory && ( preferenceHelper.keyWord != null )  ) {
                    hm = HashMap()
                    hm.put(preferenceHelper.keyWord!!, "1")
                    historyDatas.add(HistoryItem(preferenceHelper.keyWord!!, "1"))
                    database.child(preferenceHelper.userIndex!!).child("keyword_history").updateChildren(hm)
//                    tvArr[tvArrCnt].text = preferenceHelper.keyWord + "을 1번 검색하셨어요."
                }
                historyDatas.sortByDescending{ it.content }
                viewAdapter.notifyDataSetChanged()
                recyclerView.requestLayout()

                it.replaceFragment(newInstance(InvestMentFragment()))
                it.replaceBottomMenuIndex(1)
            }
        }
    }
}