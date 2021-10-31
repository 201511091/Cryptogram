package com.cm.cryptogram.base

import android.os.Bundle
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cm.cryptogram.adapter.HistoryAdapter
import com.cm.cryptogram.model.HistoryItem
import com.cm.cryptogram.utils.PreferenceHelper
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun onViewCreated()

    lateinit var binding: VB

    lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        preferenceHelper = PreferenceHelper(this)
        onViewCreated()
    }

    companion object {
        fun <T : Fragment?> newInstance(fragment: T): T {
            val args = Bundle()
            fragment!!.arguments = args
            return fragment
        }
        lateinit var jsonData: String
        lateinit var recyclerView : RecyclerView
        lateinit var viewAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        lateinit var viewManger : RecyclerView.LayoutManager
        lateinit var historyDatas : MutableList<HistoryItem>
        lateinit var historyViewText : TextView
    }
}
