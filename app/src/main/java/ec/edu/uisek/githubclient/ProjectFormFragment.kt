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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.project_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectNameEditText: EditText = view.findViewById(R.id.project_name_edit_text)
        val projectDescriptionEditText: EditText = view.findViewById(R.id.project_description_edit_text)
        val saveButton: Button = view.findViewById(R.id.save_project_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        saveButton.setOnClickListener {
            val name = projectNameEditText.text.toString().trim()
            val description = projectDescriptionEditText.text.toString().trim()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                // Create new repository and add to the list (temporarily)
                val newRepository = Repository(name, description, "Usuario Actual", 0, "https://via.placeholder.com/48x48.png?text=U")

                // Get the main activity and update the repository list
                val mainActivity = requireActivity() as MainActivity
                mainActivity.addRepository(newRepository)

                Toast.makeText(requireContext(), "¡Proyecto '$name' guardado exitosamente!", Toast.LENGTH_SHORT).show()
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