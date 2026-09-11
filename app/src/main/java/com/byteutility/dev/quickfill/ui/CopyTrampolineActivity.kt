package com.byteutility.dev.quickfill.ui

import android.app.Activity
import android.os.Bundle
import com.byteutility.dev.quickfill.util.ClipboardHelper

class CopyTrampolineActivity : Activity() {

    companion object {
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)

        val label = intent?.getStringExtra(EXTRA_LABEL).orEmpty()
        val text = intent?.getStringExtra(EXTRA_TEXT).orEmpty()

        if (text.isNotEmpty()) {
            ClipboardHelper.copyToClipboard(this, label, text, showToast = true)
        }

        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
