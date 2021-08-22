package com.projectreachout.v3.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.*

class Article {
    companion object {
        fun fromDoc(doc: DocumentSnapshot): Article {
            val userEntity: ArticleEntity? = doc.toObject(ArticleEntity::class.java)
            return Gson().fromJson(Gson().toJson(userEntity), Article::class.java)
        }
    }

    var articleId: String? = null
    var userId: String? = null
    var timeStamp: Date? = null
    var imageUrl: String? = null
    var description: String? = null
    var reactionCount: Int? = null

    private class ArticleEntity {
        @SerializedName("articleId")
        var article_id: String? = null

        @SerializedName("userId")
        var user_id: String? = null

        @SerializedName("timeStamp")
        var time_stamp: Date? = null

        @SerializedName("imageUrl")
        var image_url: String? = null
        var description: String? = null

        @SerializedName("reactionCount")
        var reaction_count: Int? = null
    }
}