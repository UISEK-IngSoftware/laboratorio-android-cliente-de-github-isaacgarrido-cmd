package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class RepositoryItemFragment : Fragment() {

    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            repository = it.getParcelable("repository")!!!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.repository_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView: TextView = view.findViewById(R.id.repository_name)
        val descriptionTextView: TextView = view.findViewById(R.id.repository_description)
        val ownerTextView: TextView = view.findViewById(R.id.repository_owner)
        val starsTextView: TextView = view.findViewById(R.id.repository_stars)

        nameTextView.text = repository.name
        descriptionTextView.text = repository.description
        ownerTextView.text = "Owner: ${repository.owner}"
        starsTextView.text = repository.stars.toString()
    }

    companion object {
        fun newInstance(repository: Repository): RepositoryItemFragment {
            val fragment = RepositoryItemFragment()
            val args = Bundle()
            args.putParcelable("repository", repository)
            fragment.arguments = args
            return fragment
        }
    }
}

private fun Bundle.putParcelable(key: String, value: Repository) {
    TODO("Not yet implemented")
}
