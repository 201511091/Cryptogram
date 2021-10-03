package com.cm.cryptogram.ui.tab

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

// [START post_class]
@IgnoreExtraProperties
data class Post(
    var id: String? = "",
    var keyword: String? = "",
    var keyword_history: Object? = null,
    var password: String? = "",
    var type: String? = ""
) {

    // [START post_to_map]
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "keyword" to keyword,
            "keyword_history" to keyword_history,
            "password" to password,
            "type" to type
        )
    }
    // [END post_to_map]
}