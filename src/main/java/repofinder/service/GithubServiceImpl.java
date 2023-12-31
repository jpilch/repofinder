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
    private final RestTemplate githubClient;
    private final MapperService mapperService;

    public GithubServiceImpl(RestTemplate githubClient, MapperService mapperService) {
        this.githubClient = githubClient;
        this.mapperService = mapperService;
    }

    @Override
    public List<Repository> findAllNonForkReposFor(String username) {
        List<GithubRepository> allRepositories = findAllRepos(username);

        List<GithubRepository> allNonForkRepositories = allRepositories
            .stream()
            .filter(GithubRepository::isNotAFork)
            .toList();

        Function<GithubRepository, Repository> toRepository = githubRepository -> {
            List<GithubBranch> githubBranches = findAllBranches(githubRepository);
            List<Repository.Branch> mappedBranches = githubBranches
                    .stream()
                    .map(mapperService::mapBranch)
                    .toList();

            return mapperService.mapRepository(githubRepository, mappedBranches);
        };

        return allNonForkRepositories
                .stream()
                .map(toRepository)
                .toList();
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
        return findAllEntities(
            (page) -> {
                String url = getRepositoriesUrl(username, page);
                return githubClient.getForObject(url, GithubRepository[].class);
            }
        );
    }

    public List<GithubBranch> findAllBranches(GithubRepository repository) {
        return findAllEntities(
            (page) -> {
                String url = getBranchesUrl(repository, page);
                return githubClient.getForObject(url, GithubBranch[].class);
            }
        );
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
