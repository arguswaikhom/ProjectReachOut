package com.projectreachout.v3.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.projectreachout.databinding.V3ArticleFragmentBinding
import com.projectreachout.v3.data.repos.ArticleRepo
import com.projectreachout.v3.data.viewmodels.ArticleVM
import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.utils.callbacks.OnArticleReactionChanges
import com.projectreachout.v3.views.adapters.ArticleListAdapter
import com.projectreachout.v3.views.components.ArticleReaction

class ArticleFragment : Fragment(), OnArticleReactionChanges {
    private val articleVM: ArticleVM by activityViewModels {
        ArticleVM.Factory(ArticleRepo())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = V3ArticleFragmentBinding.inflate(inflater, container, false)

        val adapter = ArticleListAdapter<ArticlePost>()
        view.articleListRv.adapter = adapter
        articleVM.articles.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        return view.root
    }

    /**
     * This method calls everytime the user changes reaction of any article
     * */
    override fun onArticleReactionChanges(
        reaction: ArticleReaction.Reaction?,
        article: ArticlePost
    ) {
        articleVM.updateArticleReaction(reaction, article)
    }
}