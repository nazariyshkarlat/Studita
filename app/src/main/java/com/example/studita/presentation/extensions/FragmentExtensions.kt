package com.example.studita.presentation.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.add(container, fragment, fragment::class.java.name)
    fragmentTransaction.commit()
}

fun AppCompatActivity.showFragment(fragment: Fragment){
    val fragmentTransaction =supportFragmentManager.beginTransaction()
    fragmentTransaction.show(fragment)
    fragmentTransaction.commit()
}

fun AppCompatActivity.removeFragment(fragment: Fragment){
    val fragmentTransaction =supportFragmentManager.beginTransaction()
    fragmentTransaction.remove(fragment)
    fragmentTransaction.commit()
}

fun AppCompatActivity.hideFragment(fragment: Fragment){
    val fragmentTransaction =supportFragmentManager.beginTransaction()
    fragmentTransaction.hide(fragment)
    fragmentTransaction.commit()
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
    println(fragment)
    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.remove(fragment)
    supportFragmentManager.popBackStack()
    val lastFragment = fragment.view?.id?.let { supportFragmentManager.findFragmentById(it) }
    lastFragment?.let { it1 -> fragmentTransaction.show(it1) }
    fragmentTransaction.commit()
}

fun AppCompatActivity.replaceWithAnim(fragment: Fragment, container: Int, startAnim: Int, endAnim: Int) {
    val fragmentTransaction =supportFragmentManager.beginTransaction()
    fragmentTransaction.setCustomAnimations(startAnim, endAnim, 0, 0)
    fragmentTransaction.replace(container, fragment)
    fragmentTransaction.commit()
}