package com.cm.cryptogram.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.cm.cryptogram.utils.PreferenceHelper


abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    @LayoutRes
    abstract fun layoutRes(): Int
    protected lateinit var binding: B
    private var mActivity: BaseActivity<*>? = null
    lateinit var preferenceHelper: PreferenceHelper


    protected abstract fun onViewCreated()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        preferenceHelper = PreferenceHelper(requireContext())
        onViewCreated()
    }


     fun getBaseActivity(): BaseActivity<*>? {
        return mActivity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) {
            mActivity = context
        }
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }



}