package com.projectreachout.v3.data.repos

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.projectreachout.AppController
import com.projectreachout.GeneralStatic
import com.projectreachout.R
import com.projectreachout.v3.models.Article
import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.models.User
import com.projectreachout.v3.views.components.ArticleReaction
import java.util.*

class ArticleRepo {
    interface OnResponseArticle {
        fun onResponseArticle(articleList: List<ArticlePost>)
    }

    private val tag: String by lazy { ArticleRepo::class.java.name }
    private val app: AppController by lazy { AppController.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Get latest 10 articles posted before [timeStamp]
     *
     * -> Get latest 10 articles posted before [timeStamp]
     * -> Get user details of all those article owners
     * -> Get signed in user's reaction on those article
     *
     * @param timeStamp: time stamp of the last article displaying on the screen or
     *                   the current current time by default
     * @param onResponse: callback method to update article details as we fetch more data from the DB
     * */
    fun getArticle(timeStamp: Date = Calendar.getInstance().time, onResponse: OnResponseArticle) {
        /**
         * Get the latest 10 articles posted before the [timeStamp], shorted by the posted date-time
         */
        firestore.collection(app.getString(R.string.db_col_article))
            .whereLessThan(app.getString(R.string.db_field_time_stamp), timeStamp)
            .orderBy(app.getString(R.string.db_field_time_stamp), Query.Direction.DESCENDING)
            .limit(10).get().addOnSuccessListener { articleSnapshots ->
                // If the response is empty, return an empty list
                if (articleSnapshots.isEmpty) onResponse.onResponseArticle(listOf())

                // All the distinct userIds to fetch the user details from the DB
                val userIds = mutableSetOf<String>()

                val articleEntities: MutableList<Article> = mutableListOf()
                val articles: MutableList<ArticlePost> = mutableListOf()

                // For each article, assign the articleId to the article object
                // and extract the userId
                articleSnapshots.forEach {
                    val article: Article = Article.fromDoc(it)
                    article.articleId = it.id
                    userIds.add(article.userId!!)
                    articleEntities.add(article)
                }

                // Get the user details for all the users present in the [userIds]
                firestore.collection(app.getString(R.string.db_col_user))
                    .whereIn(app.getString(R.string.db_field_user_id), userIds.toList())
                    .get().addOnSuccessListener { userSnapshots ->
                        // Put the user's details to their corresponding article
                        userSnapshots.forEach { userDoc ->
                            val user: User = User.fromDoc(userDoc)
                            articleEntities.forEach { article ->
                                if (article.userId.equals(user.userId))
                                    articles.add(ArticlePost(article, user))
                            }
                        }
                        // Return the response to display the articles on the screen
                        onResponse.onResponseArticle(articles)

                        // For each article, get information on whether the user already have given
                        // reaction to that article or not
                        articles.forEach { post ->
                            firestore.collection(app.getString(R.string.db_col_article))
                                .document(post.article.articleId!!)
                                .collection(app.getString(R.string.db_col_reaction))
                                .document(app.user.user_id).get()
                                .addOnSuccessListener { reactionDoc ->
                                    reactionDoc.exists().let {
                                        val field = app.getString(R.string.db_field_reaction)
                                        val reaction = reactionDoc.getString(field)
                                        reaction?.let {
                                            // If the user already reacted to this article,
                                            // update the article reaction and return the updated
                                            // list to display on the screen
                                            //
                                            // If the reaction is null, no need to do anything
                                            // default reaction will take care of it
                                            post.myReaction = ArticleReaction.Reaction.valueOf(it)
                                            onResponse.onResponseArticle(articles)
                                        }
                                    }
                                }
                        }
                    }
            }
    }

    fun updateArticleReaction(reaction: ArticleReaction.Reaction?, article: ArticlePost) {
        val url = "${GeneralStatic.getDomainUrl()}/on_article_reaction/"

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                article.myReaction = reaction
                Log.d(tag, "UpdateArticleReaction::Success -> $it")
            },
            Response.ErrorListener { Log.d(tag, "UpdateArticleReaction::Failed ->  $it") },
        ) {
            override fun getParams(): Map<String, String> {
                val param = mutableMapOf<String, String>()
                param["article_id"] = article.article.articleId!!
                param["user_id"] = app.firebaseAuth.uid!!
                param["action"] = reaction?.toString() ?: article.myReaction.toString()
                return param
            }
        }
        app.addToRequestQueue(stringRequest)
    }
}