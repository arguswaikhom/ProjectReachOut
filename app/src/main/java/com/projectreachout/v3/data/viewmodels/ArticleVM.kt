package com.projectreachout.v3.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projectreachout.v3.data.repos.ArticleRepo
import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.views.components.ArticleReaction

class ArticleVM(private val articleRepo: ArticleRepo) : ViewModel() {

    val articles: MutableLiveData<List<ArticlePost>> by lazy { MutableLiveData<List<ArticlePost>>() }

    init {
        articleRepo.getArticle(onResponse = object : ArticleRepo.OnResponseArticle {
            override fun onResponseArticle(articleList: List<ArticlePost>) {
                articles.value = null
                articles.value = articleList
            }
        })
    }

    fun updateArticleReaction(reaction: ArticleReaction.Reaction?, article: ArticlePost) {
        articleRepo.updateArticleReaction(reaction, article)
    }

    class Factory(private val repo: ArticleRepo) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArticleVM::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArticleVM(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}