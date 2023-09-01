package repofinder.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import repofinder.configprops.GithubServiceConfigProps;
import repofinder.model.GithubRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class GithubServiceImpl implements GithubService {
    private final GithubServiceConfigProps props;
    private final RestTemplate githubClient;

    public GithubServiceImpl(RestTemplate githubClient, GithubServiceConfigProps props) {
        this.githubClient = githubClient;
        this.props = props;
    }

    public List<GithubRepository> findAllReposFor(String username) {
        List<GithubRepository> allRepositories = findAllRepos(username);

        Consumer<GithubRepository> fetchRepositoryBranches = repository -> {
            List<GithubRepository.Branch> allBranches = findAllBranches(repository);
            repository.setBranches(allBranches);
        };

        List<GithubRepository> allNonForkRepositories = allRepositories
            .stream()
            .filter(GithubRepository::isNotAFork)
            .toList();

        allNonForkRepositories.forEach(fetchRepositoryBranches);

        return allNonForkRepositories;
    }

    private <T> List<T> findAllEntities(Function<Integer, T[]> entityFetcher, int entitiesPerPage) {
        List<T> allEntities = new ArrayList<>();

        boolean hasNextPage = true;
        int currentPage = 1;
        while (hasNextPage) {
            T[] returnedEntities = entityFetcher.apply(currentPage);
            allEntities.addAll(Arrays.asList(returnedEntities));
            hasNextPage = returnedEntities.length == entitiesPerPage;
            currentPage++;
        }

        return allEntities;
    }

    public List<GithubRepository> findAllRepos(String username) {
        return findAllEntities(
            (page) -> {
                String url = getRepositoriesUrl(username, page);
                return githubClient.getForObject(url, GithubRepository[].class);
            },
            props.getReposPerPage()
        );
    }

    public List<GithubRepository.Branch> findAllBranches(GithubRepository repository) {
        return findAllEntities(
            (page) -> {
                String url = getBranchesUrl(repository, page);
                return githubClient.getForObject(url, GithubRepository.Branch[].class);
            },
            props.getBranchesPerPage()
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
            .queryParam("per_page", props.getReposPerPage())
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
            .queryParam("per_page", props.getReposPerPage())
            .buildAndExpand(repository.getOwnerLogin(), repository.getName())
            .toUriString();
    }
}
