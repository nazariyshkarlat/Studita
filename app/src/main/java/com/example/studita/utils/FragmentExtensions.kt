package com.example.studita.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.studita.R

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = false) {
    supportFragmentManager.beginTransaction()
        .add(container, fragment, fragment::class.java.name)
        .apply {
            if(addToBackStack) addToBackStack(null)
        }
        .commit()
}

fun Fragment.addFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = false) {
    childFragmentManager.beginTransaction()
        .add(container, fragment, fragment::class.java.name)
        .apply {
            if(addToBackStack) addToBackStack(null)
        }
        .commit()
}

fun AppCompatActivity.showFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .show(fragment)
        .commit()
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .remove(fragment)
        .commit()
}

fun AppCompatActivity.hideFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .hide(fragment)
        .commit()
}

fun AppCompatActivity.navigateTo(fragment: Fragment, container: Int) {
    val lastFragmentTransaction = supportFragmentManager.beginTransaction()
    val lastFragment = supportFragmentManager.findFragmentById(container)
    lastFragment?.let { it1 -> lastFragmentTransaction.hide(it1) }
    lastFragmentTransaction.addToBackStack(fragment.javaClass.toString())
    lastFragmentTransaction.commit()
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.add(container, fragment.apply {
        if(arguments != null)
            arguments!!.putBoolean("IS_NAVIGATION", true)
        else
            arguments = bundleOf("IS_NAVIGATION" to true)
    })
    fragmentTransaction.commit()
}

fun AppCompatActivity.navigateBack(fragment: Fragment): Fragment? {
    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.remove(fragment)
    supportFragmentManager.popBackStack()
    fragmentTransaction.commitNow()
    return supportFragmentManager.findFragmentById(R.id.doubleFrameLayoutFrameLayout)
}

fun AppCompatActivity.replace(
    fragment: Fragment,
    container: Int,
    startAnim: Int = 0,
    endAnim: Int = 0,
    backStartAnim: Int = 0,
    backEndAnim: Int = 0,
    addToBackStack: Boolean
) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(startAnim, endAnim, backStartAnim, backEndAnim)
        .replace(container, fragment)
        .apply {
            if(addToBackStack) addToBackStack(null)
        }
        .commit()
}

fun Fragment.replace(
    fragment: Fragment,
    container: Int,
    startAnim: Int = 0,
    endAnim: Int = 0,
    backStartAnim: Int = 0,
    backEndAnim: Int = 0,
    addToBackStack: Boolean
) {
    childFragmentManager.beginTransaction()
        .setCustomAnimations(startAnim, endAnim, backStartAnim, backEndAnim)
        .replace(container, fragment)
        .apply {
            if(addToBackStack) addToBackStack(null)
        }
        .commit()
}
