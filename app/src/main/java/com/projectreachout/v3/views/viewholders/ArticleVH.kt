package com.projectreachout.v3.views.viewholders

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.projectreachout.R
import com.projectreachout.Utilities.TimeUtil
import com.projectreachout.databinding.V3ArticlePostBinding
import com.projectreachout.v3.models.ArticlePost
import com.projectreachout.v3.views.fragments.ArticleFragment

class ArticleVH(val view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): ArticleVH {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.v3_article_post, parent, false)
            return ArticleVH(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(post: ArticlePost) {
        val binding = V3ArticlePostBinding.bind(view)

        Glide.with(view.context).load(post.user.profileImageUrl)
            .apply(RequestOptions().circleCrop())
            .into(binding.userProfileImageIv)
        Glide.with(view.context).load(post.article.imageUrl)
            .into(binding.articlePostImageIv)

        val timestamp = TimeUtil.getTimeAgaFromSecond(post.article.timeStamp?.time!! / 1000)
        binding.timeStampTv.text = timestamp
        binding.usernameTv.text = post.user.username
        binding.totalReactCountTv.text = "${post.article.reactionCount} reacts"

        if (TextUtils.isEmpty(post.article.description)) {
            binding.articlePostDesTv.visibility = View.GONE
        } else {
            binding.articlePostDesTv.text = post.article.description
        }

        binding.articleReactionAr.updateReaction(post.myReaction)
        binding.articleReactionAr.setOnChangeReaction {
            view.findFragment<ArticleFragment>().onArticleReactionChanges(it, post)
        }
    }
}