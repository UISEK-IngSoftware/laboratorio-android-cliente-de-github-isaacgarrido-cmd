package ec.edu.uisek.githubclient

import retrofit2.Call
import retrofit2.http.*

interface GitHubApiService {
    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 10
    ): Call<SearchResponse>

    @GET("user/repos")
    fun getUserRepositories(
        @Query("sort") sort: String = "created",
        @Query("per_page") perPage: Int = 10
    ): Call<List<GitHubRepo>>

    @POST("user/repos")
    fun createRepository(@Body request: CreateRepoRequest): Call<GitHubRepo>

    @PATCH("repos/{owner}/{repo}")
    fun updateRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body request: UpdateRepoRequest
    ): Call<GitHubRepo>

    @DELETE("repos/{owner}/{repo}")
    fun deleteRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<Void>
}