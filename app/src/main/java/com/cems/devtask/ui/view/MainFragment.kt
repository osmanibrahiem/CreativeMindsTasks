package com.cems.devtask.ui.view

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.cems.devtask.R
import com.cems.devtask.databinding.DialogLinkBinding
import com.cems.devtask.databinding.FragmentMainBinding
import com.cems.devtask.helper.extensions.isNetworkConnected
import com.cems.devtask.helper.extensions.log
import com.cems.devtask.helper.extensions.openUrl
import com.cems.devtask.model.ReposItem
import com.cems.devtask.model.ResponseResult
import com.cems.devtask.ui.PaginationScrollListener
import com.cems.devtask.ui.adapter.ReposAdapter
import com.cems.devtask.ui.base.BaseFragment
import com.cems.devtask.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val layoutRes = R.layout.fragment_main
    override val viewModel: MainViewModel by viewModel()

    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private var filterName: String? = null

    private lateinit var reposAdapter: ReposAdapter
    private var currentPage = 1
    private var isLastPage = false


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
                reposAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterName = newText
                reposAdapter.filter.filter(newText)
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
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = reposAdapter
            addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
                override fun isLastPage(): Boolean {
                    return if (context.isNetworkConnected())
                        isLastPage
                    else true
                }

                override fun isLoading() = binding.swipe.isRefreshing

                override fun loadMoreItems() {
                    loadPage(page + 1)
                }

            })
        }

        binding.swipe.setOnRefreshListener(this)

        onRefresh()
    }


    var page = 1

    override fun onRefresh() {
        super.onRefresh()
        loadPage(1)
    }

    private fun loadPage(page: Int) {
        this.page = page
        log("cms_api", "load page : $page")
        viewModel.fetchRepositories(page).observe(this) { result ->
            binding.swipe.isRefreshing = result is ResponseResult.Loading
            if (result is ResponseResult.Success) {
                isLastPage = result.data.isEmpty()
                if (page == 1) {
                    reposAdapter.items.clear()
                    reposAdapter.notifyDataSetChanged()
                }
                reposAdapter.items = result.data as ArrayList<ReposItem>
            } else if (result is ResponseResult.Error) {
                log("cms_api", "Error ", result.throwable)
            }
        }
    }

}