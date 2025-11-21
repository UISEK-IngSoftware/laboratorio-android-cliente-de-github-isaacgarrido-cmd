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