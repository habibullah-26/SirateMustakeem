package com.habib.siratemustakeem.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("statusBarPadding")
fun applyStatusBarPadding(view: View, apply: Boolean) {
    if (!apply) return

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        v.setPadding(
            v.paddingLeft,
            statusBarHeight,
            v.paddingRight,
            v.paddingBottom
        )
        insets
    }
}
