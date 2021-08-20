package com.projectreachout.v3.views.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.projectreachout.Article.AddArticle.AddNewArticleFragment
import com.projectreachout.Article.GetArticle.ArticleMainFragment
import com.projectreachout.Event.GetEvent.ExpendituresMainFragment
import com.projectreachout.Event.GetMyEvent.EventMainFragment
import com.projectreachout.databinding.V3MainActivityBinding

class MainActivity : AppCompatActivity(), ArticleMainFragment.OnFragmentInteractionListener,
    AddNewArticleFragment.OnFragmentInteractionListener,
    EventMainFragment.OnFragmentInteractionListener,
    ExpendituresMainFragment.OnFragmentInteractionListener {

    private lateinit var binding: V3MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = V3MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment? ?: return
        val navController = host.navController
        binding.mainBottomNav.setupWithNavController(navController)

    }

    override fun onUpdateProgressVisibility(visibility: Int) {
    }

    override fun onFragmentInteraction(uri: Uri?) {
    }
}