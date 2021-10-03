package com.cm.cryptogram.ui.tab

import androidx.recyclerview.widget.LinearLayoutManager
import com.cm.cryptogram.R
import com.cm.cryptogram.base.BaseActivity
import com.cm.cryptogram.base.BaseFragment
import com.cm.cryptogram.databinding.FragmentKeywordBinding
import com.cm.cryptogram.databinding.FragmentSnsBinding
import org.json.JSONArray

internal class TabSnsFragment : BaseFragment<FragmentSnsBinding>() {
    override fun layoutRes(): Int = R.layout.fragment_sns
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
        val jsonObj = JSONArray(BaseActivity.jsonData)
        val list = arrayListOf<TabCard>()
        for (i in 1..(jsonObj.length() - 1)){
            val item = jsonObj.getJSONObject(i)
            if (item.getString("type") == "SNS") {
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