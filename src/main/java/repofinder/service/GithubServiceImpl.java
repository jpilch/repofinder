package repofinder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import repofinder.configurationproperties.GithubServiceConfigurationProperties;
import repofinder.model.GithubRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class GithubServiceImpl implements GithubService {
    private static final Logger logger = LoggerFactory.getLogger(GithubServiceImpl.class);
    private final GithubServiceConfigurationProperties props;
    private final RestTemplate githubClient;

    public GithubServiceImpl(RestTemplate githubClient, GithubServiceConfigurationProperties props) {
        this.githubClient = githubClient;
        this.props = props;
        System.out.println(props);
    }

    public List<GithubRepository> findAllReposFor(String username) {
        List<GithubRepository> allRepositories = findAllEntities(
            (page) -> getRepositoriesUrl(username, page),
            (url) -> githubClient.getForObject(url, GithubRepository[].class),
            props.getReposPerPage()
        );

        Consumer<GithubRepository> fetchRepositoryBranches = repository -> {
            logger.debug("repository {}, username {}", repository, username);
            List<GithubRepository.Branch> allBranches = findAllEntities(
                (page) -> getBranchesUrl(username, repository, page),
                (url) -> githubClient.getForObject(url, GithubRepository.Branch[].class),
                props.getBranchesPerPage()
            );
            repository.setBranches(allBranches);
        };

        allRepositories
            .stream()
            .filter(GithubRepository::isNotAFork)
            .forEach(fetchRepositoryBranches);

        return allRepositories;
    }

    private <T> List<T> findAllEntities(
        Function<Integer, String> urlGetter,
        Function<String, T[]> entityFetcher,
        int entitiesPerPage
    ) {
        List<T> allEntities = new ArrayList<>();

        boolean hasNextPage = true;
        int currentPage = 1;
        while (hasNextPage) {
            String url = urlGetter.apply(currentPage);
            T[] returnedEntities = entityFetcher.apply(url);
            allEntities.addAll(Arrays.asList(returnedEntities));
            hasNextPage = returnedEntities.length == entitiesPerPage;
            logger.debug("hasNextPage {}, currentPage {}, allEntities {}",
                hasNextPage, currentPage, allEntities);
            currentPage++;
        }

        return allEntities;
    }

    private String getRepositoriesUrl(String username, int currentPage) {
        String url = UriComponentsBuilder
            .newInstance()
            .path("/users")
            .path("/{username}")
            .path("/repos")
            .queryParam("type", "all")
            .queryParam("page", currentPage)
            .queryParam("per_page", props.getReposPerPage())
            .buildAndExpand(username)
            .toUriString();
        logger.debug("{}", url);
        return url;
    }

    private String getBranchesUrl(String username, GithubRepository repository, int currentPage) {
        String url = UriComponentsBuilder
            .newInstance()
            .path("/repos")
            .path("/{username}")
            .path("/{repositoryName}")
            .path("/branches")
            .queryParam("page", currentPage)
            .queryParam("per_page", props.getReposPerPage())
            .buildAndExpand(repository.getOwnerLogin(), repository.getName())
            .toUriString();
        logger.debug("{}", url);
        return url;
    }
}
