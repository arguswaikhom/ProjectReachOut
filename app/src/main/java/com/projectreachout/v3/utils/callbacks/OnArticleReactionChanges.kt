package com.projectreachout.v3.utils.callbacks

import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.views.components.ArticleReaction

interface OnArticleReactionChanges {
    fun onArticleReactionChanges(reaction: ArticleReaction.Reaction?, article: ArticlePost)
}