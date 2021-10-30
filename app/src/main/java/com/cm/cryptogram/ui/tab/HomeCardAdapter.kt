package com.cm.cryptogram.ui.tab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cm.cryptogram.R
import com.cm.cryptogram.databinding.CommunityItemRowBinding
import com.cm.cryptogram.databinding.NewItemRowBinding
import com.cm.cryptogram.databinding.SnsItemRowBinding
import com.cm.cryptogram.ui.tab.TabCard.Companion.CARD_COMMUNITY
import com.cm.cryptogram.ui.tab.TabCard.Companion.CARD_NEWS
import com.cm.cryptogram.ui.tab.TabCard.Companion.CARD_SNS
import java.lang.IllegalArgumentException


internal class HomeCardAdapter constructor() : ListAdapter<TabCard, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<TabCard>() {
        override fun areItemsTheSame(oldItem: TabCard, newItem: TabCard): Boolean {
            return oldItem.listItemId == newItem.listItemId
        }

        override fun areContentsTheSame(oldItem: TabCard, newItem: TabCard): Boolean {
            return oldItem.listItemId == newItem.listItemId
        }

    }) {



    override fun getItemViewType(position: Int) = currentList[position].type //cardList[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            CARD_NEWS -> {
                val binding = DataBindingUtil.inflate<NewItemRowBinding>(inflater, R.layout.new_item_row, parent, false)
                NewsViewHolder(binding)
            }
            CARD_SNS -> {
                val binding = DataBindingUtil.inflate<SnsItemRowBinding>(inflater, R.layout.sns_item_row, parent, false)
                SnsViewHolder(binding)
            }
            CARD_COMMUNITY -> {
                val binding = DataBindingUtil.inflate<CommunityItemRowBinding>(inflater, R.layout.community_item_row, parent, false)
                CommunityViewHolder(binding)
            }
            else -> throw IllegalArgumentException("invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //각 Item에따라 UI를 관리해주는 홀더를 생성
        when (holder) {
            is NewsViewHolder -> holder.bindView(currentList[position] as TabCard.NewsCardItem)
            is SnsViewHolder -> holder.bindView(currentList[position] as TabCard.SnsCardItem)
            is CommunityViewHolder -> holder.bindView(currentList[position] as TabCard.CommunityCardItem)
        }
    }


    inner class NewsViewHolder(private val binding: NewItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var cardItem: CardItem

        fun bindView(data: TabCard.NewsCardItem) {
            //뉴스 데이터에 대한 리스트뷰 UI를 관리한다
            cardItem = data.cardItem

            binding.tvTitle.text = cardItem.title
            binding.tvContent.text = cardItem.content
            binding.tvBtn.setOnClickListener(View.OnClickListener{
                Log.i("ADAPTER","--------------BUITTONCLICKED---------------")
                openNewTabWindow(data.cardItem.link, it.context)
            })
        }


    }
    fun openNewTabWindow(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
    inner class SnsViewHolder(private val binding: SnsItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var cardItem: CardItem

        fun bindView(data: TabCard.SnsCardItem) {
            //sns 데이터에 대한 리스트뷰 UI를 관리한다
            cardItem = data.cardItem
            binding.tvTitle.text = cardItem.title
            binding.tvContent.text = cardItem.content
        }


    }

    inner class CommunityViewHolder(private val binding: CommunityItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var cardItem: CardItem

        fun bindView(data: TabCard.CommunityCardItem) {
            //커뮤니티 데이터에 대한 리스트뷰 UI를 관리한다
            cardItem = data.cardItem
            binding.tvTitle.text = cardItem.title
            binding.tvContent.text = cardItem.content
        }


    }

}