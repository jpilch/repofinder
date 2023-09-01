package repofinder.service;

import repofinder.model.GithubRepository;

import java.util.List;

public interface GithubService {
    List<GithubRepository> findAllNonForkReposFor(String username);
}
