package com.example.studita.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.studita.R

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int) {
    supportFragmentManager.beginTransaction()
        .add(container, fragment, fragment::class.java.name)
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
    endAnim: Int = 0
) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(startAnim, endAnim, 0, 0)
        .replace(container, fragment)
        .commit()
}