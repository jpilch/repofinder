package repofinder.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class GithubServiceImpl implements GithubService {
    private final WebClient client;
    private final MapperService mapperService;

    public GithubServiceImpl(WebClient client, MapperService mapperService) {
        this.client = client;
        this.mapperService = mapperService;
    }

    @Override
    public List<Repository> findAllNonForkReposFor(String username) {

        return List.of();
    }
}
