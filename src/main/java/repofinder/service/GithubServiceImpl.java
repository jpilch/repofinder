package repofinder.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private final MapperService mapperService;

    public GithubServiceImpl(MapperService mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public List<Repository> findAllNonForkReposFor(String username) {
        return List.of();
    }

    private <T> List<T> findAllEntities(Function<Integer, T[]> entityFetcher) {
        List<T> allEntities = new ArrayList<>();

        boolean hasNextPage = true;
        int currentPage = 1;
        while (hasNextPage) {
            T[] returnedEntities = entityFetcher.apply(currentPage);
            allEntities.addAll(Arrays.asList(returnedEntities));
            hasNextPage = returnedEntities.length == 100;
            currentPage++;
        }

        return allEntities;
    }

    public List<GithubRepository> findAllRepos(String username) {
        return List.of();
    }

    public List<GithubBranch> findAllBranches(GithubRepository repository) {
        return List.of();
    }

    private String getRepositoriesUrl(String username, int currentPage) {
        return UriComponentsBuilder
            .newInstance()
            .path("/users")
            .path("/{username}")
            .path("/repos")
            .queryParam("type", "all")
            .queryParam("page", currentPage)
            .queryParam("per_page", 100)
            .buildAndExpand(username)
            .toUriString();
    }

    private String getBranchesUrl(GithubRepository repository, int currentPage) {
        return UriComponentsBuilder
            .newInstance()
            .path("/repos")
            .path("/{username}")
            .path("/{repositoryName}")
            .path("/branches")
            .queryParam("page", currentPage)
            .queryParam("per_page", 100)
            .buildAndExpand(repository.owner().login(), repository.name())
            .toUriString();
    }
}
