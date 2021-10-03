package com.cm.cryptogram.ui.tab

internal sealed class TabCard(val type : Int, val listItemId: String) {
    companion object {
        const val CARD_NEWS = 0
        const val CARD_SNS = 1
        const val CARD_COMMUNITY = 2
    }

    data class NewsCardItem(val cardItem: CardItem) : TabCard(CARD_NEWS, cardItem.toString())
    data class SnsCardItem(val cardItem: CardItem) : TabCard(CARD_SNS, cardItem.toString())
    data class CommunityCardItem(val cardItem: CardItem) : TabCard(CARD_COMMUNITY, cardItem.toString())

}