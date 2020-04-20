package com.example.studita.presentation.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int) {
    supportFragmentManager.beginTransaction()
        .add(container, fragment, fragment::class.java.name)
        .commit()
}

fun AppCompatActivity.showFragment(fragment: Fragment){
    supportFragmentManager.beginTransaction()
        .show(fragment)
        .commit()
}

fun AppCompatActivity.removeFragment(fragment: Fragment){
    supportFragmentManager.beginTransaction()
        .remove(fragment)
        .commit()
}

fun AppCompatActivity.hideFragment(fragment: Fragment){
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
    fragmentTransaction.add(container, fragment)
    fragmentTransaction.commit()
}

fun AppCompatActivity.navigateBack(fragment: Fragment){
    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.remove(fragment)
    supportFragmentManager.popBackStack()
    val lastFragment = fragment.view?.id?.let { supportFragmentManager.findFragmentById(it) }
    lastFragment?.let { it1 -> fragmentTransaction.show(it1) }
    fragmentTransaction.commit()
}

fun AppCompatActivity.replace(fragment: Fragment, container: Int, startAnim: Int = 0, endAnim: Int = 0) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(startAnim, endAnim, 0, 0)
        .replace(container, fragment)
        .commit()
}