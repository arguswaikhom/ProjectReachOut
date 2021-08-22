package com.projectreachout.v3.models

import com.projectreachout.v3.views.components.ArticleReaction

class ArticlePost(var article: Article, var user: User) {
    var myReaction: ArticleReaction.Reaction? = null
}