package com.cems.devtask.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.cems.devtask.BR
import com.cems.devtask.helper.LocaleManager

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected abstract val layoutRes: Int
    protected abstract val viewModel: VM

    protected lateinit var binding: VB

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let {
            LocaleManager.setLocale(it)
        } ?: base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        if (savedInstanceState == null) {
            initActions()
        }
    }

    protected open fun initViews() {
//        setContentView(layoutRes)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }

    protected open fun initActions() {}
}