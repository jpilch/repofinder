package repofinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;
import repofinder.service.GithubService;
import repofinder.service.GithubServiceImpl;
import repofinder.service.MapperService;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GithubServiceImplTests {

    private RestTemplate githubClient;
    private MapperService mapperService;
    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        githubClient = mock(RestTemplate.class);
        mapperService = mock(MapperService.class);
        githubService = new GithubServiceImpl(githubClient, mapperService);
    }

    @Test
    public void callsCorrectAPIEndpoints() throws Exception {
        GithubRepository mockRepo = new GithubRepository( "repofinder", new GithubRepository.Owner("john"), false);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl = "/repos/john/repofinder/branches?page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo});
        given(githubClient.getForObject(mockBranchesUrl, GithubBranch[].class))
            .willReturn(new GithubBranch[] {});

        githubService.findAllNonForkReposFor("john");

        verify(githubClient).getForObject(mockReposUrl, GithubRepository[].class);
        verify(githubClient).getForObject(mockBranchesUrl, GithubBranch[].class);
    }

    @Test
    public void callsBranchesEndpointForEveryNonForkRepo() throws Exception {
        GithubRepository.Owner owner = new GithubRepository.Owner("john");
        GithubRepository mockRepo1 = new GithubRepository("repo1", owner, false);
        GithubRepository mockRepo2 = new GithubRepository("repo3", owner, true);

        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl1 = "/repos/john/repo1/branches?page=1&per_page=100";
        String mockBranchesUrl2 = "/repos/john/repo2/branches?page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo1, mockRepo2});
        given(githubClient.getForObject(mockBranchesUrl1, GithubBranch[].class))
            .willReturn(new GithubBranch[] {});

        githubService.findAllNonForkReposFor("john");

        verify(githubClient, times(1))
            .getForObject(mockBranchesUrl1, GithubBranch[].class);
        verify(githubClient, times(0))
            .getForObject(mockBranchesUrl2, GithubBranch[].class);
    }

    @Test
    public void filtersForkRepositories() throws Exception {
        GithubRepository.Owner owner = new GithubRepository.Owner("john");
        GithubRepository mockRepo = new GithubRepository( "repofinder", owner, true);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo});

        List<Repository> result = githubService.findAllNonForkReposFor("john");

        assertEquals(result.size(), 0);
    }

    @Test
    public void callsMapperServiceWithEachRepositoryAndItsBranches() throws Exception {
        GithubRepository mockRepo1 = new GithubRepository(
                "repo1",
                new GithubRepository.Owner("john"),
                false
        );
        GithubBranch mockBranch = new GithubBranch("master", new GithubBranch.Commit("ad0e68f2"));
        Repository.Branch mappedBranch = new Repository.Branch("master", "ad0e68f2");

        given(githubClient.getForObject("/users/john/repos?type=all&page=1&per_page=100", GithubRepository[].class))
                .willReturn(new GithubRepository[] {mockRepo1});
        given(githubClient.getForObject("/repos/john/repo1/branches?page=1&per_page=100", GithubBranch[].class))
                .willReturn(new GithubBranch[] {mockBranch});
        given(mapperService.mapBranch(mockBranch)).willReturn(mappedBranch);

        githubService.findAllNonForkReposFor("john");

        verify(mapperService).mapBranch(mockBranch);
        verify(mapperService).mapRepository(mockRepo1, List.of(mappedBranch));
    }

    @Test
    public void returnsResultsMappedByMapperService() throws Exception {
        GithubRepository mockRepo = new GithubRepository(
                "repo1",
                new GithubRepository.Owner("john"),
                false
        );
        GithubBranch mockBranch = new GithubBranch("master", new GithubBranch.Commit("ad0e68f2"));
        Repository.Branch mappedBranch = new Repository.Branch("master", "ad0e68f2");
        Repository mappedRepository = new Repository("john", "repofinder", List.of(mappedBranch));

        given(githubClient.getForObject("/users/john/repos?type=all&page=1&per_page=100", GithubRepository[].class))
                .willReturn(new GithubRepository[] {mockRepo});
        given(githubClient.getForObject("/repos/john/repo1/branches?page=1&per_page=100", GithubBranch[].class))
                .willReturn(new GithubBranch[] {mockBranch});
        given(mapperService.mapBranch(mockBranch)).willReturn(mappedBranch);
        given(mapperService.mapRepository(mockRepo, List.of(mappedBranch)))
                .willReturn(mappedRepository);

        List<Repository> result = githubService.findAllNonForkReposFor("john");

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), mappedRepository);
    }
}
