package com.cems.devtask.ui.view

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.cems.devtask.R
import com.cems.devtask.databinding.DialogLinkBinding
import com.cems.devtask.databinding.FragmentMainBinding
import com.cems.devtask.helper.extensions.openUrl
import com.cems.devtask.model.ResponseResult
import com.cems.devtask.ui.adapter.ReposAdapter
import com.cems.devtask.ui.adapter.ReposLoadStateAdapter
import com.cems.devtask.ui.base.BaseFragment
import com.cems.devtask.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val layoutRes = R.layout.fragment_main
    override val viewModel: MainViewModel by viewModel()

    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private var filterName: String? = null

    private lateinit var reposAdapter: ReposAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        requireActivity().menuInflater.inflate(R.menu.menu_search, menu)

        searchItem = menu.findItem(R.id.action_search)
        searchView = SearchView(
            (activity as? AppCompatActivity)?.supportActionBar?.themedContext ?: requireContext()
        )
        searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
            .setBackgroundColor(Color.TRANSPARENT)
        searchView.queryHint = getString(R.string.title_search)
        searchItem.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_ALWAYS)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterName = query
//                categoriesAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterName = newText
//                categoriesAdapter.filter.filter(newText)
                return false
            }
        })

        if (!TextUtils.isEmpty(filterName)) {
            searchItem.expandActionView()
            searchView.setQuery(filterName, true)
            searchView.clearFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(filterName)) {
            searchItem.expandActionView()
            searchView.setQuery(filterName, true)
            searchView.clearFocus()
        }
    }

    override fun initActions(view: View) {
        super.initActions(view)

        reposAdapter = ReposAdapter { reposItem ->
            val dialogBinding = DialogLinkBinding.inflate(layoutInflater)
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(dialogBinding.root)
            dialogBinding.btnOwner.setOnClickListener {
                dialog.dismiss()
                openUrl(reposItem.owner?.htmlUrl)
            }
            dialogBinding.btnRepos.setOnClickListener {
                dialog.dismiss()
                openUrl(reposItem.htmlUrl)
            }
            dialog.show()
        }

        binding.list.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = reposAdapter.withLoadStateHeaderAndFooter(
                header = ReposLoadStateAdapter { reposAdapter.retry() },
                footer = ReposLoadStateAdapter { reposAdapter.retry() }
            )
        }

        binding.swipe.setOnRefreshListener { reposAdapter.retry() }


        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            reposAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipe.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel.fetchRepositories().collectLatest {
                reposAdapter.submitData(it)
            }
        }
    }

}