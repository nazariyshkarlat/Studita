package com.studita.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.RotateDrawable
import android.os.Build
import android.text.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.util.CoilUtils
import com.studita.R
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.presentation.draw.AvaDrawer
import com.studita.presentation.fragments.main.MainFragment
import com.studita.presentation.listeners.FabScrollImpl
import com.studita.presentation.listeners.FabScrollListener
import com.studita.presentation.listeners.OnViewSizeChangeListener
import com.studita.presentation.listeners.OnViewSizeChangeListenerImpl
import com.studita.presentation.views.CustomSnackbar
import java.util.regex.Pattern
import kotlin.reflect.KClass


fun View.getRelativeLeft(parent: ViewGroup): Int {
    val offsetViewBounds = Rect()
    getDrawingRect(offsetViewBounds)
    parent.offsetDescendantRectToMyCoords(this, offsetViewBounds)
    return offsetViewBounds.left
}

fun NestedScrollView.setOnScrollChangeFabListener(fabScrollListener: FabScrollListener) {
    this.setOnScrollChangeListener(FabScrollImpl(fabScrollListener))
}

inline fun <reified T : View>View.postExt(crossinline block: (T) -> Unit) {
    post { block.invoke(this as T) }
}

fun View.isContains(rx: Int, ry: Int): Boolean {
    val l = IntArray(2)
    getLocationOnScreen(l)
    val x = l[0]
    val y = l[1]
    val w = width
    val h = height
    return !(rx < x || rx > x + w || ry < y || ry > y + h)
}

fun View.isContainsAnotherView(secondView: View): Boolean {
    val secondRect = Rect()
    secondView.getHitRect(secondRect)

    val firstRect = Rect()
    getHitRect(firstRect)

    return (secondRect.contains(firstRect.left, secondRect.centerY()) || secondRect.contains(firstRect.right, secondRect.centerY())) &&
            (secondRect.contains(secondRect.centerX(), firstRect.top) || secondRect.contains(secondRect.centerX(), firstRect.bottom))
}

fun View.childContainsParentX(rx: Int): Boolean {
    val bounds = Rect()
    getHitRect(bounds)
    bounds.left -= marginStart
    bounds.right += marginEnd
    return bounds.contains(rx, bounds.centerY())
}

fun View.childContainsParentY(ry: Int): Boolean {
    val bounds = Rect()
    getHitRect(bounds)
    bounds.top -= marginTop
    bounds.bottom += marginBottom
    return bounds.contains(bounds.centerX(), ry)
}

fun View.childIsInParentX(leftX: Int, rightX: Int): Boolean {
    val bounds = Rect()
    getHitRect(bounds)
    bounds.left -= marginStart
    bounds.right += marginEnd
    return bounds.contains(leftX, bounds.centerY()) && bounds.contains(rightX, bounds.centerY())
}

fun View.childIsInParentY(topY: Int, bottomY: Int): Boolean {
    val bounds = Rect()
    getHitRect(bounds)
    bounds.top -= marginTop
    bounds.bottom += marginBottom
    return bounds.contains(bounds.centerX(), topY) && bounds.contains(bounds.centerX(), bottomY)
}

fun View.centerIsInAnotherView(secondView: View): Boolean{
    val secondRect = Rect()
    secondView.getHitRect(secondRect)

    val firstRect = Rect()
    getHitRect(firstRect)

    return secondRect.contains(firstRect.centerX(), firstRect.centerY())
}

fun View.asNotificationIndicator(notificationsAreChecked: Boolean) {
    if (notificationsAreChecked || !UserUtils.isLoggedIn()) {
        this.animate().scaleX(0F).scaleY(0F).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                this@asNotificationIndicator.visibility = View.GONE
            }
        }).start()
    } else {
        with(this) {
            visibility = android.view.View.VISIBLE
            scaleX = 0F
            scaleY = 0F
            this.animate().scaleX(1F).scaleY(1F).setListener(null).start()
        }
    }
}

fun EditText.limitLength(maxLength: Int) {
    filters = arrayOf(*filters, InputFilter.LengthFilter(maxLength))
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard() {
    (this as Context).showKeyboard()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard() {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

fun Context.getAppCompatActivity(): AppCompatActivity? {
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

fun View.getAppCompatActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun <T : View> ViewGroup.allViewsOfTypeT(type: KClass<T>, f: (T) -> Unit) {
    this.forEach { child ->
        if (type.isInstance(child)) f(child as T)
        if (child is ViewGroup) child.allViewsOfTypeT(type, f)
    }
}

inline fun <reified T : View> ViewGroup.allViewsOfTypeT(noinline f: (T) -> Unit) =
    allViewsOfTypeT(T::class, f)

fun <T : View> ViewGroup.getAllViewsOfTypeT(type: KClass<T>): List<T> {
    val allViews = ArrayList<T>()
    this.forEach { child ->
        if (type.isInstance(child)) allViews.add(child as T)
        if (child is ViewGroup) allViews.addAll(child.getAllViewsOfTypeT(type))
    }
    return allViews
}

inline fun <reified T : View> ViewGroup.getAllViewsOfTypeT() = getAllViewsOfTypeT<T>(T::class)

fun ImageView.fillAvatar(avatarLink: String?, userName: String, userId: Int, withPlaceholder: Boolean = true, isDarkerPlaceholder: Boolean = false) {
    if (avatarLink == null) {
        CoilUtils.clear(this)
        AvaDrawer.drawAvatar(this, userName, userId)
    } else {
        loadUrlAvatar(avatarLink, withPlaceholder, isDarkerPlaceholder)
    }
}

fun ImageView.loadUrlAvatar(avatarLink: String, withPlaceholder: Boolean = true, isDarkerPlaceholder: Boolean = false) {
    val request = ImageRequest.Builder(this.context)
        .data(avatarLink)
        .target(this)
        .apply {
            if(withPlaceholder) placeholder(if(!isDarkerPlaceholder) R.drawable.avatar_placeholder else R.drawable.avatar_placeholder_darker)
        }
        .transformations(CircleCropTransformation())
        .build()
    ImageLoader(context).enqueue(request)
}

fun ImageView.loadAvatarDrawable(@DrawableRes drawable: Int) {
    this.load(drawable) {
        crossfade(true)
        placeholder(R.drawable.avatar_placeholder)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.loadAvatarBitmap(bitmap: Bitmap) {
    this.load(bitmap) {
        crossfade(true)
        placeholder(R.drawable.avatar_placeholder)
        transformations(CircleCropTransformation())
    }
}

fun TextView.isEllipsized(): Boolean {

    // Check if ellipsizing the text is enabled
    if (ellipsize == null || TextUtils.TruncateAt.MARQUEE == ellipsize
    ) return false

    // Retrieve the layout in which the text is rendered
    val layout = layout ?: return false

    // Iterate all lines to search for ellipsized text
    for (line in 0 until layout.lineCount) {

        // Check if characters have been ellipsized away within this line of text
        if (layout.getEllipsisCount(line) > 0) {
            return true
        }
    }
    return false
}

fun TextView.removeLastChar(){
    this.text = this.text.substring(
        0,
        this.text.length-1
    )
}

fun TextView.replaceLastChar(newChar: Char){
    removeLastChar()
    append(newChar.toString())
}

fun View.disableAllItems(accept: (View) -> Boolean = {true}) {
    if(accept(this))
        this.isEnabled = false
    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            child.disableAllItems()
        }
    }
}

fun TextView.clear(){
    this.text = null
}

fun View.setAllClickable(clickable: Boolean) {
    isClickable = clickable
    isLongClickable = clickable
    if (this is ViewGroup) children.forEach { child -> child.setAllClickable(clickable) }
}

fun View.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags: Int = systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        systemUiVisibility = flags
    }
}

fun View.setLightStatusBar() {
    var flags: Int = systemUiVisibility
    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    systemUiVisibility = flags
}

fun View.setLightNavigation(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        var flags: Int = systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        systemUiVisibility = flags
    }
}


fun View.clearLightNavigation(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags: Int = systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        systemUiVisibility = flags
    }
}

fun TextView.animateRefreshButton() {
    val refreshDrawable = (this.compoundDrawables[0] as RotateDrawable)

    refreshDrawable.mutate()

    val anim = ObjectAnimator.ofPropertyValuesHolder(
        refreshDrawable,
        PropertyValuesHolder.ofInt("level", 0, 10000)
    ).setDuration(resources.getInteger(R.integer.refresh_animation_time).toLong())

    anim.interpolator = FastOutSlowInInterpolator()
    anim.setAutoCancel(true)
    anim.start()
}

fun TextView.animateExpandIcon(backwards: Boolean = false) {
    val refreshDrawable = (this.compoundDrawables[0] as RotateDrawable)

    refreshDrawable.mutate()

    val anim = ObjectAnimator.ofPropertyValuesHolder(
        refreshDrawable,
        if(!backwards) PropertyValuesHolder.ofInt("level", refreshDrawable.level, 10000)
        else PropertyValuesHolder.ofInt("level", refreshDrawable.level, 0)
    ).setDuration(resources.getInteger(R.integer.select_course_scene_duration).toLong())

    anim.interpolator = FastOutSlowInInterpolator()
    anim.setAutoCancel(true)
    anim.start()
}

fun injectParts(context: Context, text: String, partsToInject: List<String>) : Pair<CharSequence, String?> {
    var insideBrackets: String? = null
    val m =
        Pattern.compile("\\{.*?\\}").matcher(text)
    var spanIndex = 1
    val builder = SpannableStringBuilder()
    val textSpanParts: ArrayList<SpannableString> = ArrayList(text.split(
        "\\{.*?\\}".toRegex()
    ).map { span ->
        SpannableString(span) })
    while (m.find()) {
        insideBrackets =
            partsToInject[m.group(0).replace(
                """[{}]""".toRegex(),
                ""
            ).toInt()]
        textSpanParts.add(
            spanIndex + spanIndex-1,
            insideBrackets.createSpannableString(
                color = ThemeUtils.getGreenColor(context)
            )
        )
        spanIndex++
    }
    textSpanParts.forEach { part -> builder.append(part) }
    return if (spanIndex == 1)  text to insideBrackets else builder to insideBrackets
}

fun View.isClick(event: MotionEvent) = event.action == MotionEvent.ACTION_UP && kotlin.math.abs(translationX) <= 5F && kotlin.math.abs(translationY) <= 5F && (event.eventTime - event.downTime) < 500F

fun ImageView.loadSVG(url: String, @DrawableRes placeholder: Int){
    val imageLoader = ImageLoader.Builder(context)
        .componentRegistry {
            add(SvgDecoder(context))
        }
        .build()
    val request = ImageRequest.Builder(this.context)
        .data(url)
        .placeholder(placeholder)
        .target(this)
        .build()
    imageLoader.enqueue(request)
}

fun loadSVGIntoMultipleTargets(imageViews: List<ImageView>, url: String, @DrawableRes placeholder: Int){
    val imageLoader = ImageLoader.Builder(imageViews.first().context)
        .componentRegistry {
            add(SvgDecoder(imageViews.first().context))
        }
        .build()
    val request = ImageRequest.Builder(imageViews.first().context)
        .data(url)
        .target(
            onStart = {
                imageViews.forEach {
                    it.setImageResource(placeholder)
                }
            },
            onError = {
                      imageViews.forEach{
                          it.setImageResource(0)
                      }
            },
            onSuccess = {drawable->
                imageViews.forEach {
                    it.setImageDrawable(drawable)
                }
        })
        .build()
    imageLoader.enqueue(request)
}

fun showSubscribeEmailSnackbar(status: SubscribeEmailResultStatus, context: Context, onMainLayout: Boolean) {
    val snackbar = CustomSnackbar(context)
    when (status) {
        is SubscribeEmailResultStatus.Success -> {
            if (status.result.subscribe) {
                snackbar.show(
                    context.resources.getString(
                        R.string.subscribe_email,
                        com.studita.utils.TextUtils.encryptEmail(status.result.email!!)
                    ),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(snackbar.context),
                        ContextCompat.getColor(snackbar.context, R.color.white)
                    ),
                    ContextCompat.getColor(snackbar.context, R.color.black),
                    context.resources.getInteger(R.integer.subscribe_email_snackbar_duration)
                        .toLong(),
                    bottomMarginExtra = if(onMainLayout) MainFragment.getBottomMarginExtraSnackbar(context) else 0
                )
            } else {
                snackbar.show(
                    context.resources.getString(R.string.unsubscribe_email),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(snackbar.context),
                        ContextCompat.getColor(snackbar.context, R.color.white)
                    ),
                    ContextCompat.getColor(snackbar.context, R.color.black),
                    context.resources.getInteger(R.integer.unsubscribe_email_snackbar_duration)
                        .toLong(),
                    bottomMarginExtra = if(onMainLayout) MainFragment.getBottomMarginExtraSnackbar(context) else 0
                )
            }
        }
        is SubscribeEmailResultStatus.NoConnection -> {
            if (!UserUtils.userData.isSubscribed) {
                snackbar.show(
                    context.resources.getString(R.string.offline_subscribe_email),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(snackbar.context),
                        ContextCompat.getColor(snackbar.context, R.color.white)
                    ),
                    ContextCompat.getColor(snackbar.context, R.color.black),
                    context.resources.getInteger(R.integer.offline_subscribe_email_snackbar_duration)
                        .toLong(),
                    bottomMarginExtra = if(onMainLayout) MainFragment.getBottomMarginExtraSnackbar(context) else 0
                )
            } else {
                snackbar.show(
                    context.resources.getString(R.string.offline_unsubscribe_email),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(snackbar.context),
                        ContextCompat.getColor(snackbar.context, R.color.white)
                    ),
                    ContextCompat.getColor(snackbar.context, R.color.black),
                    context.resources.getInteger(R.integer.offline_subscribe_email_snackbar_duration)
                        .toLong(),
                    bottomMarginExtra = if(onMainLayout) MainFragment.getBottomMarginExtraSnackbar(context) else 0
                )
            }
        }
    }
}