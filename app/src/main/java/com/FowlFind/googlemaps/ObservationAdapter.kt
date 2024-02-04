package com.FowlFind.googlemaps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ObservationsAdapter(var observations: List<Observations>) : RecyclerView.Adapter<ObservationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.detailImageView)
        val speciesText: TextView = view.findViewById(R.id.detailSpeciesText)
        val dateText: TextView = view.findViewById(R.id.detailDateText)
        val notesText: TextView = view.findViewById(R.id.detailNotesText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.observation_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val observation = observations[position]
        holder.speciesText.text = observation.species
        holder.dateText.text = observation.observationDate
        holder.notesText.text = observation.notes
        Picasso.get().load(observation.capturedImage).into(holder.imageView)
    }

    override fun getItemCount() = observations.size
}
