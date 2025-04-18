package com.example.stajh2test.ui.components

import android.content.Intent
import android.net.Uri

fun openUrlInBrowser(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}