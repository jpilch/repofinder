package repofinder.service;

import repofinder.model.Repository;

import java.util.List;

public interface GithubService {
    List<Repository> findAllNonForkReposFor(String username);
}
