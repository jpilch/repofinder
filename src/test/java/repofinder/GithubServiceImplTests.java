package repofinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import repofinder.model.GithubRepository;
import repofinder.service.GithubService;
import repofinder.service.GithubServiceImpl;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GithubServiceImplTests {

    private RestTemplate githubClient;

    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        githubClient = mock(RestTemplate.class);
        this.githubService = new GithubServiceImpl(githubClient);
    }

    @Test
    public void findAllNonForkReposFor_CallsCorrectApiEndpoints() throws Exception {
        GithubRepository mockRepo = new GithubRepository( "john", "repofinder", List.of(), false);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl = "/repos/john/repofinder/branches?page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo});
        given(githubClient.getForObject(mockBranchesUrl, GithubRepository.Branch[].class))
            .willReturn(new GithubRepository.Branch[] {});

        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");

        verify(githubClient).getForObject(mockReposUrl, GithubRepository[].class);
        verify(githubClient).getForObject(mockBranchesUrl, GithubRepository.Branch[].class);
    }

    @Test
    public void findAllNonForkReposFor_CallsBranchesEndpointForEveryNonForkRepo() throws Exception {
        GithubRepository mockRepo1 = new GithubRepository("john", "a-big-repo", List.of(), false);
        GithubRepository mockRepo2 = new GithubRepository( "alice", "for-you", List.of(), false);
        GithubRepository mockRepo3 = new GithubRepository( "bob", "fork-repo", List.of(), true);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl1 = "/repos/john/a-big-repo/branches?page=1&per_page=100";
        String mockBranchesUrl2 = "/repos/alice/for-you/branches?page=1&per_page=100";
        String mockBranchesUrl3 = "/repos/bob/for-repo/branches?page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo1, mockRepo2, mockRepo3});
        given(githubClient.getForObject(mockBranchesUrl1, GithubRepository.Branch[].class))
            .willReturn(new GithubRepository.Branch[] {});
        given(githubClient.getForObject(mockBranchesUrl2, GithubRepository.Branch[].class))
            .willReturn(new GithubRepository.Branch[] {});

        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");

        verify(githubClient, times(1))
            .getForObject(mockBranchesUrl1, GithubRepository.Branch[].class);
        verify(githubClient, times(1))
            .getForObject(mockBranchesUrl2, GithubRepository.Branch[].class);
        verify(githubClient, times(0))
            .getForObject(mockBranchesUrl3, GithubRepository.Branch[].class);
    }

    @Test
    public void findAllNonForkReposFor_FiltersForkRepos() throws Exception {
        GithubRepository mockRepo = new GithubRepository( "john", "repofinder", List.of(), true);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl = "/repos/john/repofinder/branches?page=1&per_page=100";

        given(githubClient.getForObject(mockReposUrl, GithubRepository[].class))
            .willReturn(new GithubRepository[] {mockRepo});
        given(githubClient.getForObject(mockBranchesUrl, GithubRepository.Branch[].class))
            .willReturn(new GithubRepository.Branch[] {});

        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");

        assertEquals(result.size(), 0);
    }
}
