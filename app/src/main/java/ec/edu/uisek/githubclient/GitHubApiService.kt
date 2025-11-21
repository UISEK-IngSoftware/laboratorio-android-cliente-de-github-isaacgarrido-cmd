package ec.edu.uisek.githubclient

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 10
    ): Call<SearchResponse>
}