package com.projectreachout.v3.views.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.getResourceIdOrThrow
import com.projectreachout.R
import com.projectreachout.databinding.V3ArticleReactionComponentBinding

class ArticleReaction(context: Context, attrSet: AttributeSet) : LinearLayout(context, attrSet) {
    enum class Reaction { like, love }

    private var onChangeReaction: ((Reaction?) -> Unit)? = null
    private val binding: V3ArticleReactionComponentBinding
    private val srcLikeActive: Int
    private val srcLikeInactive: Int
    private val srcLoveActive: Int
    private val srcLoveInactive: Int
    private var currentReaction: Reaction? = null

    init {
        val li = LayoutInflater.from(context)
        binding = V3ArticleReactionComponentBinding.inflate(li, this, true)

        val attrs = context.obtainStyledAttributes(attrSet, R.styleable.ArticleReaction, 0, 0)
        srcLikeActive = attrs.getResourceIdOrThrow(R.styleable.ArticleReaction_srcLikeActive)
        srcLikeInactive = attrs.getResourceIdOrThrow(R.styleable.ArticleReaction_srcLikeInactive)
        srcLoveActive = attrs.getResourceIdOrThrow(R.styleable.ArticleReaction_srcLoveActive)
        srcLoveInactive = attrs.getResourceIdOrThrow(R.styleable.ArticleReaction_srcLoveInactive)
        reset()

        binding.likeReactIbtn.setOnClickListener { handleOnChangeReaction(Reaction.like) }
        binding.loveReactIbtn.setOnClickListener { handleOnChangeReaction(Reaction.love) }

        attrs.recycle()
    }

    /**
     * Set both the reaction to it's inactive state
     * */
    private fun reset() {
        currentReaction = null
        binding.likeReactIbtn.setImageResource(srcLikeInactive)
        binding.loveReactIbtn.setImageResource(srcLoveInactive)
    }

    /**
     * Handle the article reaction changes by
     * -> Updating the new reaction immediately on the UI and
     * -> Calling the on change callback method to update to the DB
     * */
    private fun handleOnChangeReaction(reaction: Reaction) {
        // If the user re-clicked the same reaction, update reaction as null
        // reset() will take care of it
        updateReaction(if (reaction == currentReaction) null else reaction)

        onChangeReaction?.let { it(currentReaction) }
    }

    fun updateReaction(reaction: Reaction?) {
        reset()
        if (reaction == null) return

        // On changing the reaction from one reaction type to another
        // like to love || love to like
        if (reaction == Reaction.like && currentReaction != Reaction.like) {
            binding.likeReactIbtn.setImageResource(srcLikeActive)
        } else if (reaction == Reaction.love && currentReaction != Reaction.love) {
            binding.loveReactIbtn.setImageResource(srcLoveActive)
        }
        currentReaction = reaction
    }

    fun setOnChangeReaction(onChangeReaction: (Reaction?) -> Unit) {
        this.onChangeReaction = onChangeReaction
    }
}