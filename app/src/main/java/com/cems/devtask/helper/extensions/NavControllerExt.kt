package com.cems.devtask.helper.extensions

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewParent
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.forEach
import androidx.customview.widget.Openable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.cems.devtask.R

fun NavController.setUpWith(
    navigationView: NavigationView,
    bottomNavigationView: BottomNavigationView,
    defaultArgs: HashMap<Int, Bundle>? = null,
    listener: ((MenuItem) -> Unit)? = null
) {
    navigationView.setNavigationItemSelectedListener { item ->
        val parent: ViewParent? = navigationView.parent
        val parentOfParent: ViewParent? = parent?.parent
        when {
            parent is Openable -> {
                parent.close()
            }
            parentOfParent is Openable -> {
                parentOfParent.close()
            }
            else -> {
                val bottomSheetBehavior = findBottomSheetBehavior(navigationView)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        onNavDestinationSelected(item, defaultArgs?.get(item.itemId), listener)
    }
    bottomNavigationView.setOnNavigationItemSelectedListener { item ->
        onNavDestinationSelected(item, defaultArgs?.get(item.itemId), listener)
    }
    addOnDestinationChangedListener { _, destination, _ ->
        navigationView.menu.findItem(destination.id)?.isChecked = true
        bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
    }
}

private fun findBottomSheetBehavior(view: View): BottomSheetBehavior<*>? {
    val params = view.layoutParams
    if (params !is CoordinatorLayout.LayoutParams) {
        val parent = view.parent
        return if (parent is View) {
            findBottomSheetBehavior(parent)
        } else null
    }
    val behavior = params.behavior
    return if (behavior !is BottomSheetBehavior<*>) {
        // We hit a CoordinatorLayout, but the View doesn't have the BottomSheetBehavior
        null
    } else behavior
}

private fun matchDestination(
    destination: NavDestination?,
    @IdRes destId: Int
): Boolean {
    var currentDestination: NavDestination? = destination
    while (currentDestination?.id != destId && currentDestination?.parent != null) {
        currentDestination = currentDestination.parent
    }
    return currentDestination?.id == destId
}

fun NavController.onNavDestinationSelected(
    item: MenuItem,
    args: Bundle? = null,
    listener: ((MenuItem) -> Unit)? = null
): Boolean {
    if (matchDestination(currentDestination, item.itemId)) return false
    val startDestination = findStartDestination()?.id ?: graph.startDestination
    if (matchDestination(findStartDestination(), item.itemId)) {
        popBackStack(startDestination, false)
        return true
    }
    val builder = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.nav_default_enter_anim)
        .setExitAnim(R.anim.nav_default_exit_anim)
        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
    if (item.order and Menu.CATEGORY_SECONDARY == 0) {
        builder.setPopUpTo(startDestination, false)
    }
    val options = builder.build()
    return try {
        navigate(item.itemId, args, options)
        true
    } catch (e: IllegalArgumentException) {
        listener?.invoke(item)
        false
    }
}

private fun NavController.findStartDestination(): NavDestination? {
    var startDestination: NavDestination? = graph
    while (startDestination is NavGraph) {
        val parent = startDestination
        startDestination = parent.findNode(parent.startDestination)
    }
    return startDestination
}