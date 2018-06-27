package com.alexbezhan.instagram.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.*
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alexbezhan.instagram.GlideApp
import com.alexbezhan.instagram.R
import java.util.*

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all { it.text.isNotEmpty() }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }
    inputs.forEach { it.addTextChangedListener(watcher) }
    btn.isEnabled = inputs.all { it.text.isNotEmpty() }
}

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

fun ImageView.loadUserPhoto(photoUrl: String?) =
        ifNotDestroyed {
            GlideApp.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
        }

fun ImageView.loadImage(image: String?, hideOnNull: Boolean = false) =
        ifNotDestroyed {
            if (hideOnNull) {
                visibility =
                        if (image == null) View.GONE
                        else View.VISIBLE

                GlideApp.with(this).load(image).centerCrop().into(this)
            } else {
                GlideApp.with(this).load(image).centerCrop().into(this)
            }
        }

fun View.setClickableDeep(clickable: Boolean) {
    isEnabled = clickable
    if (this is ViewGroup) {
        repeat(childCount) { idx ->
            getChildAt(idx).setClickableDeep(clickable)
        }
    }
}

fun TextView.setCommentText(username: String, comment: String,
                            timestamp: Date? = null,
                            onUsernameClick: View.OnClickListener? = null) {
    val usernameSpannable = SpannableString(username)
    usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    usernameSpannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            onUsernameClick?.onClick(widget)
        }

        override fun updateDrawState(ds: TextPaint?) {}
    }, 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


    text = SpannableStringBuilder().apply {
        append(usernameSpannable).append(" ").append(comment)
        if (timestamp != null) {
            val relativeDateTime = DateUtils.getRelativeTimeSpanString(
                    timestamp.time,
                    System.currentTimeMillis() + 60 * 1000 * 40,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE)
                    .replace(Regex(" ago$"), "")
            val dateTimeSpannable = SpannableString(relativeDateTime)
            dateTimeSpannable.setSpan(ForegroundColorSpan(
                    ContextCompat.getColor(context, R.color.grey)),
                    0,
                    dateTimeSpannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            append(" ").append(dateTimeSpannable)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}

fun View.hideSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun RecyclerView.disableChangeAnimation() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}