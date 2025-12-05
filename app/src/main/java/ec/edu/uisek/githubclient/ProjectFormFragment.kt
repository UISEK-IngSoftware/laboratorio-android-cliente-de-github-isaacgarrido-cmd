package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProjectFormFragment : Fragment() {

    private var repository: Repository? = null
    private var position: Int = -1

    companion object {
        fun newInstance(repository: Repository? = null, position: Int = -1): ProjectFormFragment {
            val fragment = ProjectFormFragment()
            val args = Bundle()
            args.putParcelable("repository", repository)
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.project_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = arguments?.getParcelable("repository")
        position = arguments?.getInt("position", -1) ?: -1

        val projectNameEditText: EditText = view.findViewById(R.id.project_name_edit_text)
        val projectDescriptionEditText: EditText = view.findViewById(R.id.project_description_edit_text)
        val saveButton: Button = view.findViewById(R.id.save_project_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        // Pre-fill fields if editing
        repository?.let {
            projectNameEditText.setText(it.name)
            projectDescriptionEditText.setText(it.description)
        }

        saveButton.setOnClickListener {
            val name = projectNameEditText.text.toString().trim()
            val description = projectDescriptionEditText.text.toString().trim()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                val mainActivity = requireActivity() as MainActivity
                if (repository != null && position != -1) {
                    // Editing existing repository
                    mainActivity.updateRepositoryApi(repository!!.owner, repository!!.name, name, description)
                } else {
                    // Creating new repository
                    mainActivity.createRepository(name, description)
                }
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}