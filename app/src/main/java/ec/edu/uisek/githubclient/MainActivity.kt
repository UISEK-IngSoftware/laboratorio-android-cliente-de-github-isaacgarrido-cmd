package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private val repositories = mutableListOf<Repository>()

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

        adapter = RepositoryAdapter(repositories, { position -> editRepository(position) }, { position -> deleteRepository(position) })
        recyclerView.adapter = adapter

        // Fetch repositories from GitHub API
        fetchRepositories()

        // Set up button to navigate to form
        val addProjectButton: FloatingActionButton = findViewById(R.id.add_project_button)
        addProjectButton.setOnClickListener {
            showFragment(ProjectFormFragment.newInstance())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun addRepository(repository: Repository) {
        repositories.add(0, repository)
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    private fun editRepository(position: Int) {
        val repository = repositories[position]
        showFragment(ProjectFormFragment.newInstance(repository, position))
    }

    private fun deleteRepository(position: Int) {
        repositories.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    fun updateRepository(position: Int, repository: Repository) {
        repositories[position] = repository
        adapter.notifyItemChanged(position)
    }

    private fun fetchRepositories() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(GitHubApiService::class.java)
        val call = apiService.searchRepositories("language:kotlin", perPage = 10)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { searchResponse ->
                        val fetchedRepos = searchResponse.items.map { gitHubRepo ->
                            Repository(
                                name = gitHubRepo.name,
                                description = gitHubRepo.description ?: "No description",
                                language = gitHubRepo.language ?: "Unknown",
                                owner = gitHubRepo.owner.login,
                                stars = gitHubRepo.stargazers_count,
                                avatarUrl = gitHubRepo.owner.avatar_url
                            )
                        }
                        repositories.clear()
                        repositories.addAll(fetchedRepos)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error fetching repositories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}