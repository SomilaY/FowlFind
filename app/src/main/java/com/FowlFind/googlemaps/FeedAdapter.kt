package com.FowlFind.googlemaps

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FeedAdapter(private val items: MutableList<FeedItem>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
         val imageView: ImageView = view.findViewById(R.id.ivImage)
         val textView: TextView = view.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = items[position]
        Log.d("Firestore", "Binding view holder for item: $item")
        holder.textView.text = item.comment
        Picasso.get().load(item.imageUri).into(holder.imageView)
    }


    override fun getItemCount() = items.size

    fun addItem(item: FeedItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}
