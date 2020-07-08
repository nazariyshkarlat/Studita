package com.example.studita.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.presentation.animations.ProgressBarAnimation
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.listeners.FabScrollImpl
import com.example.studita.presentation.listeners.FabScrollListener
import com.example.studita.presentation.listeners.OnViewSizeChangeListener
import com.example.studita.presentation.listeners.OnViewSizeChangeListenerImpl
import com.example.studita.presentation.views.CustomProgressBar
import kotlin.reflect.KClass

fun NestedScrollView.setOnScrollChangeFabListener(fabScrollListener: FabScrollListener) {
    this.setOnScrollChangeListener(FabScrollImpl(fabScrollListener))
}

fun View.asNotificationIndicator(notificationsAreChecked: Boolean){
    if(notificationsAreChecked || !UserUtils.isLoggedIn()){
        this.animate().scaleX(0F).scaleY(0F).setListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                this@asNotificationIndicator.visibility = View.GONE
            }
        }).start()
    }else{
        with(this) {
            visibility = android.view.View.VISIBLE
            scaleX = 0F
            scaleY = 0F
            this.animate().scaleX(1F).scaleY(1F).setListener(null).start()
        }
    }
}

fun EditText.limitLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard(){
    (this as Context).showKeyboard()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(){
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.getAppCompatActivity(): AppCompatActivity?{
    var context = this
    while (this is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = this.baseContext
    }
    return null
}

fun View.setOnViewSizeChangeListener(viewSizeChangeListener: OnViewSizeChangeListener): View.OnLayoutChangeListener {
    val listener = OnViewSizeChangeListenerImpl(viewSizeChangeListener, this)
    this.addOnLayoutChangeListener(listener)
    return listener
}

fun ViewGroup.makeView(@LayoutRes layoutResId: Int): View =
    LayoutInflater.from(this.context).inflate(layoutResId, this, false)

fun View.onViewCreated(block: () ->Unit){
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {
            block()
            this@onViewCreated.viewTreeObserver.removeOnGlobalLayoutListener(this);
        }
    })
}

fun View.getAppCompatActivity(): AppCompatActivity?{
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun CustomProgressBar.animateProgress(toPercent: Float, onAnimEnd: ()->Unit = {}, duration: Long = 300L, delay: Long = 0L, fromPercent: Float = this.percentProgress) {
    val progressAnimation = ProgressBarAnimation(
            this,
            fromPercent,
            toPercent
    )
    progressAnimation.startOffset = delay
    progressAnimation.interpolator =
            androidx.interpolator.view.animation.FastOutSlowInInterpolator()
    progressAnimation.duration = duration
    progressAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            this@animateProgress.clearAnimation()
            onAnimEnd.invoke()
        }

        override fun onAnimationStart(animation: Animation?) {}
    })
    this.startAnimation(progressAnimation)
}
fun <T : View> ViewGroup.allViewsOfTypeT(type: KClass<T>, f: (T) -> Unit) {
    this.forEach {child->
        if (type.isInstance(child)) f(child as T)
        if (child is ViewGroup) child.allViewsOfTypeT(type, f)
    }
}

inline fun <reified T : View> ViewGroup.allViewsOfTypeT(noinline f: (T) -> Unit)
        = allViewsOfTypeT(T::class, f)

fun ImageView.fillAvatar(avatarLink: String?, userName: String, userId: Int){
    if (avatarLink == null) {
        Glide
            .with(this.context)
            .clear(this)
        AvaDrawer.drawAvatar(this, userName, userId)
    } else {
        Glide
                .with(this)
                .load(avatarLink)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(this)
    }
}