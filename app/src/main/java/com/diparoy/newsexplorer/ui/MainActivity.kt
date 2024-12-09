@file:Suppress("DEPRECATION")
package com.diparoy.newsexplorer.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diparoy.newsexplorer.R
import com.diparoy.newsexplorer.Resource
import com.diparoy.newsexplorer.adapters.ArticleAdapter
import com.diparoy.newsexplorer.adapters.ItemClickListener
import com.diparoy.newsexplorer.databinding.ActivityMainBinding
import com.diparoy.newsexplorer.db.Article
import com.diparoy.newsexplorer.mvvm.NewsRepo
import com.diparoy.newsexplorer.mvvm.NewsViewModel
import com.diparoy.newsexplorer.mvvm.NewsViewModelFac
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: ArticleAdapter
    private lateinit var rv: RecyclerView
    private lateinit var pb: ProgressBar
    private var isClicked: Boolean = false
    private var addingResponselist = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            showExitConfirmationDialog()
        }

        val repository = NewsRepo()
        val factory = NewsViewModelFac(repository, application)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        if (nInfo != null && nInfo.isConnected) {
            initializeUI()
            loadCategoryNews("everything")
        } else {
            showToast("Connect to the Internet")
        }
    }

    private fun initializeUI() {
        val breakingCat: CircleImageView = binding.btn1
        val businessCat: CircleImageView = binding.btn2
        val sportCat: CircleImageView = binding.btn3
        val entertainmentCat: CircleImageView = binding.btn4
        val techCat: CircleImageView = binding.btn5
        val scienceCat: CircleImageView = binding.btn6
        val healthCat: CircleImageView = binding.btn7
        val searchView = binding.searchView

        rv = binding.recyclerview
        pb = binding.progressBar

        setUpRecyclerView()

        val catlistener = View.OnClickListener {
            when (it.id) {
                R.id.btn_1 -> {
                    isClicked = true
                    viewModel.getCategory("general")
                    loadCategoryNews("general")
                    setUpRecyclerView()
                }
                R.id.btn_2 -> {
                    isClicked = true
                    viewModel.getCategory("business")
                    loadCategoryNews("business")
                    setUpRecyclerView()
                }
                R.id.btn_3 -> {
                    isClicked=true
                    viewModel.getCategory("sports")
                    loadCategoryNews("sports")
                    setUpRecyclerView()
                }
                R.id.btn_4 -> {
                    isClicked = true
                    viewModel.getCategory("entertainment")
                    loadCategoryNews("entertainment")
                    setUpRecyclerView()
                }
                R.id.btn_5 -> {
                    isClicked = true
                    viewModel.getCategory("tech")
                    loadCategoryNews("tech")
                    setUpRecyclerView()
                }
                R.id.btn_6 -> {
                    isClicked=true
                    viewModel.getCategory("science")
                    loadCategoryNews("science")
                    setUpRecyclerView()
                }
                R.id.btn_7 -> {
                    isClicked = true
                    viewModel.getCategory("health")
                    loadCategoryNews("health")
                    setUpRecyclerView()
                }
            }
        }
        techCat.setOnClickListener(catlistener)
        breakingCat.setOnClickListener(catlistener)
        businessCat.setOnClickListener(catlistener)
        sportCat.setOnClickListener(catlistener)
        scienceCat.setOnClickListener(catlistener)
        entertainmentCat.setOnClickListener(catlistener)
        healthCat.setOnClickListener(catlistener)


        searchView.queryHint = "Search News"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                newFilterItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = ArticleAdapter()
        newsAdapter.setItemClickListener(this)
        rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun loadCategoryNews(category: String) {
        viewModel.getCategory(category)
        viewModel.categoryNews.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        addingResponselist.clear()
                        addingResponselist.addAll(newsResponse.articles)
                        newsAdapter.setList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.i("com.diparoy.newsexplorer.ui.MainActivity", message.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun showProgressBar() {
        pb.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        pb.visibility = View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onItemClicked(position: Int, article: Article) {
        val intent = Intent(this@MainActivity, NewsFullActivity::class.java)
        intent.putExtra("url", article.url)
        startActivity(intent)
    }

    private fun newFilterItems(query: String?) {
        showProgressBar()
        val filteredList = ArrayList<Article>()
        val searchText = query.orEmpty().toLowerCase()

        CoroutineScope(Dispatchers.Default).launch {
            for (article in addingResponselist) {
                if (article.title?.toLowerCase()?.contains(searchText) == true) {
                    filteredList.add(article)
                }
            }
            withContext(Dispatchers.Main) {
                hideProgressBar()
                newsAdapter.setList(filteredList)
            }
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are You Sure You Want to Exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                onBackPressed()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val positiveButton = builder.show().getButton(DialogInterface.BUTTON_POSITIVE)
        val yesColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.navy))
        val yesButtonText = SpannableString("Yes")
        yesButtonText.setSpan(yesColorSpan, 0, yesButtonText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        positiveButton.text = yesButtonText

        val negativeButton = builder.show().getButton(DialogInterface.BUTTON_NEGATIVE)
        val noColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.navy))
        val noButtonText = SpannableString("No")
        noButtonText.setSpan(noColorSpan, 0, noButtonText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        negativeButton.text = noButtonText
    }
}