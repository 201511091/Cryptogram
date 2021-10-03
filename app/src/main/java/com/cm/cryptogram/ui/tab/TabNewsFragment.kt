package com.cm.cryptogram.ui.tab

import androidx.recyclerview.widget.LinearLayoutManager
import com.cm.cryptogram.L
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity.Companion.jsonData
import com.cm.cryptogram.base.BaseFragment
import com.cm.cryptogram.databinding.FragmentKeywordBinding
import com.cm.cryptogram.databinding.FragmentNewsBinding
import ninja.sakib.jsonq.JSONQ
import org.json.JSONArray
import org.json.JSONObject

internal class TabNewsFragment : BaseFragment<FragmentNewsBinding>() {
    override fun layoutRes(): Int = R.layout.fragment_news
    private val homeAdapter by lazy { HomeCardAdapter() }

    override fun onViewCreated() {
        initRecyclerView()
        onLoad()
    }

    private fun initRecyclerView() {
        //리스트뷰 셋팅
        binding.rvContent.run {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }

    private fun onLoad(){
        //데이터 더미데이터 생성
        val jsonObj = JSONArray(jsonData)
        val list = arrayListOf<TabCard>()
        for (i in 1..(jsonObj.length() - 1)){
            val item = jsonObj.getJSONObject(i)
            if (item.getString("type") == "news") {
                list.add(TabCard.NewsCardItem(CardItem(0,
                    "제목: "+ item.getString("title"),
                    "내용: " + item.getString("text"),
                    item.getString("link")
                )))
            }
        }
        homeAdapter.submitList(list)
    }


}