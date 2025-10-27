package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private val repositories = mutableListOf(
        Repository("Android App", "A sample Android application", "elGuapo1", 150),
        Repository("Kotlin Utils", "Utility functions for Kotlin", "Loquinloquero", 89),
        Repository("UI Components", "Reusable UI components", "Isaac", 234),
        Repository("Data Structures", "Implementation of common data structures", "Garrido", 67),
        Repository("Networking Library", "HTTP client library", "ElFLaKo", 312)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.repositories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RepositoryAdapter(repositories)
        recyclerView.adapter = adapter

        // Set up button to navigate to form
        val addProjectButton: Button = findViewById(R.id.add_project_button)
        addProjectButton.setOnClickListener {
            showFragment(ProjectFormFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun addRepository(repository: Repository) {
        repositories.add(repository)
        adapter.notifyItemInserted(repositories.size - 1)
    }
}