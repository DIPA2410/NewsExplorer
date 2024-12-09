package com.diparoy.newsexplorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diparoy.newsexplorer.R
import com.diparoy.newsexplorer.Utils
import com.diparoy.newsexplorer.db.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleHolder>() {
    private var newsList = listOf<Article>()
    private var listener: ItemClickListener? = null

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    fun setList(articles: List<Article>) {
        this.newsList = articles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_recycler_row, parent, false)
        return ArticleHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = newsList[position]
        holder.bind(article)
    }

    inner class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSource: TextView = itemView.findViewById(R.id.tvSource)
        private val tvDescription: TextView = itemView.findViewById(R.id.newsDescription)
        private val tvPublishedAt: TextView = itemView.findViewById(R.id.publishedDate)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.pb)
        val imageView: ImageView = itemView.findViewById(R.id.articleImage)
        private val readButton: Button = itemView.findViewById(R.id.readButton)

        init {
            readButton.setOnClickListener {
                listener?.onItemClicked(adapterPosition, newsList[adapterPosition])
            }
        }

        fun bind(article: Article) {
            textTitle.text = article.title
            tvSource.text = article.source?.name
            tvDescription.text = article.description
            tvPublishedAt.text = Utils.DateFormat(article.publishedAt)
            loadImageWithPicasso(article.urlToImage, imageView, progressBar)
        }
    }

    private fun loadImageWithPicasso(url: String?, imageView: ImageView, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        if (url.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            imageView.setImageResource(R.drawable.baseline_image_24)
        } else {
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_hide_image_24)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }
                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                    }
                })
        }
    }
}

interface ItemClickListener {
    fun onItemClicked(position: Int, article: Article)
}