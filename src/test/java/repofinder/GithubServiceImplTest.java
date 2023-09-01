package repofinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import repofinder.configprops.GithubServiceConfigProps;
import repofinder.model.GithubRepository;
import repofinder.service.GithubService;
import repofinder.service.GithubServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GithubServiceImplTest {

    private GithubServiceConfigProps props;
    private RestTemplate githubClient;

    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        props = mock(GithubServiceConfigProps.class);
        githubClient = mock(RestTemplate.class);
        this.githubService = new GithubServiceImpl(githubClient, props);
    }

    @Test
    public void findAllNonForkReposFor_CallsCorrectApiEndpoints() throws Exception {
        GithubRepository mockRepo = new GithubRepository(
            "john",
            "repofinder",
            List.of(),
            false);
        String mockReposUrl = "/users/john/repos?type=all&page=1&per_page=100";
        String mockBranchesUrl = "/repos/john/repofinder/branches?page=1&per_page=100";

        given(props.getReposPerPage()).willReturn(100);
        given(props.getBranchesPerPage()).willReturn(100);
        given(githubClient.getForObject(
            mockReposUrl,
            Object.class))
            .willReturn(Optional.of(List.of()));

        List<GithubRepository> result = githubService.findAllNonForkReposFor("john");

        assertEquals(result.size(), 0);
        verify(githubClient).getForObject(mockReposUrl, GithubRepository[].class);
        verify(githubClient).getForObject(mockReposUrl, GithubRepository.Branch[].class);
    }
}
