package ec.edu.uisek.githubclient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private val repositories = mutableListOf<Repository>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: GitHubApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("GitHubPrefs", Context.MODE_PRIVATE)

        setupApiService()

        // Set up RecyclerView
        recyclerView = findViewById(R.id.repositories_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RepositoryAdapter(repositories, { position -> editRepository(position) }, { position ->
            val repo = repositories[position]
            deleteRepositoryApi(repo.owner, repo.name, position)
        })
        recyclerView.adapter = adapter

        // Fetch repositories from GitHub API
        fetchRepositories()

        // Set up button to navigate to form
        val addProjectButton: Button = findViewById(R.id.add_project_button)
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

    fun createRepository(name: String, description: String) {
        val username = sharedPreferences.getString("github_username", null)
        if (username == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        val request = CreateRepoRequest(name = name, description = description)
        val call = apiService.createRepository(request)
        call.enqueue(object : Callback<GitHubRepo> {
            override fun onResponse(call: Call<GitHubRepo>, response: Response<GitHubRepo>) {
                if (response.isSuccessful) {
                    response.body()?.let { repo ->
                        val newRepo = Repository(
                            name = repo.name,
                            description = repo.description ?: "No description",
                            language = repo.language ?: "Unknown",
                            owner = repo.owner.login,
                            stars = repo.stargazers_count,
                            avatarUrl = repo.owner.avatar_url
                        )
                        addRepository(newRepo)
                        Toast.makeText(this@MainActivity, "Repository created successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error creating repository", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GitHubRepo>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateRepositoryApi(owner: String, repoName: String, newName: String, description: String) {
        val username = sharedPreferences.getString("github_username", null)
        if (username == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        val request = UpdateRepoRequest(name = newName, description = description)
        val call = apiService.updateRepository(owner, repoName, request)
        call.enqueue(object : Callback<GitHubRepo> {
            override fun onResponse(call: Call<GitHubRepo>, response: Response<GitHubRepo>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Repository updated successfully", Toast.LENGTH_SHORT).show()
                    // Refresh list or update local
                    fetchRepositories()
                } else {
                    Toast.makeText(this@MainActivity, "Error updating repository", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GitHubRepo>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteRepositoryApi(owner: String, repoName: String, position: Int) {
        val username = sharedPreferences.getString("github_username", null)
        if (username == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        val call = apiService.deleteRepository(owner, repoName)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    deleteRepository(position)
                    Toast.makeText(this@MainActivity, "Repository deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error deleting repository", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupApiService() {
        val username = sharedPreferences.getString("github_username", null)
        val password = sharedPreferences.getString("github_password", null)
        val client = if (username != null && password != null) {
            OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val credentials = okhttp3.Credentials.basic(username, password)
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", credentials)
                        .build()
                    chain.proceed(request)
                })
                .build()
        } else {
            OkHttpClient.Builder().build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GitHubApiService::class.java)
    }

    private fun fetchRepositories() {
        val username = sharedPreferences.getString("github_username", null)
        if (username != null) {
            // Fetch user's repositories
            val call = apiService.getUserRepositories(perPage = 10)
            call.enqueue(object : Callback<List<GitHubRepo>> {
                override fun onResponse(call: Call<List<GitHubRepo>>, response: Response<List<GitHubRepo>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { repos ->
                            val fetchedRepos = repos.map { gitHubRepo ->
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

                override fun onFailure(call: Call<List<GitHubRepo>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Fallback to search if not logged in
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
}