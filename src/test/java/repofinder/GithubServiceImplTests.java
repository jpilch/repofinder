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
        this.githubService = new GithubServiceImpl(githubClient, mapperService);
    }

    @Test
    public void shouldCallCorrectAPIEndpoints() throws Exception {
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
    public void shouldCallsBranchesEndpointForEveryNonForkRepo() throws Exception {
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
//
//    @Test
//    public void findAllNonForkReposFor_FiltersForkRepos() throws Exception {
//        GithubRepository mockRepo = new GithubRepository( "john", "repofinder", List.of(), true);
//        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
//        String mockBranchesUrl = "/repos/john/repofinder/branches?page=1&per_page=100";
//
//        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
//            .willReturn(new GithubRepository[] {mockRepo});
//        given(githubClient.getForObject(mockBranchesUrl, GithubRepository.Branch[].class))
//            .willReturn(new GithubRepository.Branch[] {});
//
//        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");
//
//        assertEquals(result.size(), 0);
//    }
//
//    @Test
//    public void findAllNonForkReposFor_SetsBranchesForEachRepo() throws Exception {
//        GithubRepository mockRepo1 = new GithubRepository("john", "repo1", List.of(), false);
//        GithubRepository mockRepo2 = new GithubRepository( "alice", "repo2", List.of(), false);
//        GithubRepository.Branch mockBranch1 = new GithubRepository.Branch("master", "ad0e68f2");
//        GithubRepository.Branch mockBranch2 = new GithubRepository.Branch("develop", "8fc62157");
//        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
//        String mockBranchesUrl1 = "/repos/john/repo1/branches?page=1&per_page=100";
//        String mockBranchesUrl2 = "/repos/alice/repo2/branches?page=1&per_page=100";
//
//        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
//            .willReturn(new GithubRepository[] {mockRepo1, mockRepo2});
//        given(githubClient.getForObject(mockBranchesUrl1, GithubRepository.Branch[].class))
//            .willReturn(new GithubRepository.Branch[] {mockBranch1});
//        given(githubClient.getForObject(mockBranchesUrl2, GithubRepository.Branch[].class))
//            .willReturn(new GithubRepository.Branch[] {mockBranch2});
//
//        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");
//        GithubRepository repo1 = result.get(0);
//        GithubRepository repo2 = result.get(1);
//
//        assertEquals(result.size(), 2);
//        assertEquals(repo1.getBranches().size(), 1);
//        assertEquals(repo2.getBranches().size(), 1);
//        assertEquals(repo1.getBranches().get(0), mockBranch1);
//        assertEquals(repo2.getBranches().get(0), mockBranch2);
//    }
}
