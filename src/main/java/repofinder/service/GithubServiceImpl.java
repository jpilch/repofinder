package repofinder.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class GithubServiceImpl implements GithubService {
    private static final String REPOS_URL = "/users/%s/repos?type=all&per_page=100";
    private static final String BRANCHES_URL = "/repos/%s/%s/branches?per_page=100";

    private final WebClient client;
    private final MapperService mapperService;

    public GithubServiceImpl(WebClient client, MapperService mapperService) {
        this.client = client;
        this.mapperService = mapperService;
    }

    @Override
    public Flux<Repository> findAllNonForkReposFor(String username) {
        Function<GithubRepository, Mono<Repository>> mapper = repository -> client
                .get()
                .uri(String.format(BRANCHES_URL, username, repository.name()))
                .retrieve()
                .bodyToFlux(GithubBranch.class)
                .map(mapperService::mapBranch)
                .collectList()
                .map(branches ->  mapperService.mapRepository(repository, branches));

        return client
                .get()
                .uri(String.format(REPOS_URL, username))
                .retrieve()
                .bodyToFlux(GithubRepository.class)
                .flatMap(mapper);
    }
}
