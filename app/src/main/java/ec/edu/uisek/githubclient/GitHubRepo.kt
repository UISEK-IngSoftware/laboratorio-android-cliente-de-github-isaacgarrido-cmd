
















































































package ec.edu.uisek.githubclient

data class GitHubRepo(
    val name: String,
    val description: String?,
    val language: String?,
    val owner: Owner,
    val stargazers_count: Int
)

data class Owner(
    val login: String,
    val avatar_url: String
)

data class SearchResponse(
    val items: List<GitHubRepo>
)

data class CreateRepoRequest(
    val name: String,
    val description: String? = null,
    val private: Boolean = false
)

data class UpdateRepoRequest(
    val name: String? = null,
    val description: String? = null,
    val private: Boolean? = null
)