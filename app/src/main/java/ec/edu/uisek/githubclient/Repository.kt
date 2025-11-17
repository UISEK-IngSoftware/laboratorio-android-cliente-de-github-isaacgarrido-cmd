package ec.edu.uisek.githubclient

data class Repository(
    val name: String,
    val description: String,
    val owner: String,
    val stars: Int,
    val avatarUrl: String
)