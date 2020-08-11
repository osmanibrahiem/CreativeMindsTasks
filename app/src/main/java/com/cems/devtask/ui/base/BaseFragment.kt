package com.cems.devtask.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionInflater
import com.cems.devtask.BR
import com.cems.devtask.R
import com.cems.devtask.helper.extensions.hideKeyboard
import com.cems.devtask.helper.extensions.setSupportActionBar

abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment(),
    SwipeRefreshLayout.OnRefreshListener {

    protected var root: View? = null
    protected abstract val layoutRes: Int
    protected abstract val viewModel: VM

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = initViews(inflater, container)

            root?.let { view ->
                initToolbar(view)
                initActions(view)
            }
        }
        (root?.parent as? ViewGroup?)?.removeView(root)
        return root
    }

    protected open fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
//        root = inflater.inflate(layoutRes, container, false)
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        root = binding.root
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
        return root
    }

    protected open fun initToolbar(view: View) {
        view.findViewById<Toolbar?>(R.id.toolbar)?.let {
            setSupportActionBar(it)
        }
    }

    protected open fun initActions(view: View) {}

    open fun onBackPressed(): Boolean = false

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

    override fun onRefresh() {}
}