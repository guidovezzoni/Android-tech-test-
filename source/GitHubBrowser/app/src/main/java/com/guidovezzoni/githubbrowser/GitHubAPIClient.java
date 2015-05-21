package com.guidovezzoni.githubbrowser;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

public class GitHubAPIClient {
    public static final String URL = "https://api.github.com";

    // json data class
    static class Item {
        String sha;
        Commit commit;
        String url;

        public Item(String sha, Commit commit, String url) {
            this.sha = sha;
            this.commit = commit;
            this.url = url;
        }

    }

    // json data class
    static class Commit {
        Author author;
        String message;

        public Commit(Author author, String message) {
            this.author = author;
            this.message = message;
        }
    }

    // json data class
    static class Author {
        String name;
        String date;

        public Author(String name, String date) {
            this.name = name;
            this.date = date;
        }
    }

    /*
    * Handles the retrofix call for GitHub/repos
    * */
    interface GitHubServiceInterface {

        @GET("/repos/{owner}/{repo}/commits")
        void commits(@Path("owner") String owner, @Path("repo") String repo, Callback<List<Item>> callback);

    }

    /*
    * Returns a service instance, that will query the GitHub API when needed
    * */
    public static GitHubServiceInterface getService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .build();

        return restAdapter.create(GitHubServiceInterface.class);
    }
}
