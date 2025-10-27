package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.repository_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.repository_description)
        val ownerTextView: TextView = itemView.findViewById(R.id.repository_owner)
        val starsTextView: TextView = itemView.findViewById(R.id.repository_stars)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_item, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        holder.nameTextView.text = repository.name
        holder.descriptionTextView.text = repository.description
        holder.ownerTextView.text = "Owner: ${repository.owner}"
        holder.starsTextView.text = repository.stars.toString()
    }

    override fun getItemCount(): Int = repositories.size
}