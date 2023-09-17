package repofinder.service;

import reactor.core.publisher.Flux;
import repofinder.model.Repository;

import java.util.List;

public interface GithubService {
    Flux<Repository> findAllNonForkReposFor(String username);
}
