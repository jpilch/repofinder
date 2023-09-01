package repofinder.service;

import repofinder.model.GithubRepository;

import java.util.List;

public interface GithubService {
    List<GithubRepository> findAllReposFor(String username);
}
