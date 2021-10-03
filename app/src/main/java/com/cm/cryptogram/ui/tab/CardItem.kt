package com.cm.cryptogram.ui.tab

import com.cm.cryptogram.R

internal data class CardItem(
    val photoUri: Int = R.mipmap.ic_launcher,
    val title: String,
    val content: String,
    val link: String
)