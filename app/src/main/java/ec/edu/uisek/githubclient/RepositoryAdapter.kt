package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RepositoryAdapter(
    private val repositories: List<Repository>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.user_avatar)
        val nameTextView: TextView = itemView.findViewById(R.id.repository_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.repository_description)
        val ownerTextView: TextView = itemView.findViewById(R.id.repository_owner)
        val languageTextView: TextView = itemView.findViewById(R.id.repository_language)
        val starsTextView: TextView = itemView.findViewById(R.id.repository_stars)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_item, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        Glide.with(holder.itemView.context)
            .load(repository.avatarUrl)
            .circleCrop()
            .into(holder.avatarImageView)
        holder.nameTextView.text = repository.name
        holder.descriptionTextView.text = repository.description
        holder.ownerTextView.text = "Propietario: ${repository.owner}"
        holder.languageTextView.text = repository.language
        holder.starsTextView.text = repository.stars.toString()

        holder.editButton.setOnClickListener { onEdit(position) }
        holder.deleteButton.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = repositories.size
}