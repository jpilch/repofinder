package repofinder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import repofinder.controller.RepofinderController;
import repofinder.model.GithubRepository;
import repofinder.service.GithubService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(RepofinderController.class)
public class RepofinderControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    GithubService githubService;

    @Test
    public void findAllNonForkReposFor_NonExistentUser() throws Exception {
        String mockErrorResponseBody = "{\"message\": \"Not found\"}";
        String mockExceptionMessage = "User not found";
        HttpStatusCode mockExceptionStatus = HttpStatusCode.valueOf(404);
        HttpClientErrorException userNotFoundException = new HttpClientErrorException(
            mockExceptionStatus,
            mockExceptionMessage,
            mockErrorResponseBody.getBytes(),
            StandardCharsets.UTF_8
        );

        given(githubService.findAllNonForkReposFor(any(String.class)))
            .willThrow(userNotFoundException);

        mockMvc.perform(get("/bob")
            .accept("application/json"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.Message")
                .value("Not found"))
            .andExpect(jsonPath("$.status").value(404));

        verify(githubService).findAllNonForkReposFor(any(String.class));
    }

    @Test
    public void findAllNonForkReposFor_UnsupportedContentType() throws Exception {
        given(githubService.findAllNonForkReposFor(any(String.class)))
            .willReturn(List.of());

        mockMvc.perform(get("/bob")
                .accept("application/xml"))
            .andExpect(status().isNotAcceptable())
            .andExpect(jsonPath("$.Message")
                .value("No acceptable representation"))
            .andExpect(jsonPath("$.status").value(406));

        verify(githubService, times(0))
            .findAllNonForkReposFor(any(String.class));
    }

    @Test
    public void findAllNonForkReposFor_ValidUser() throws Exception {
        GithubRepository.Branch mockBranch = new GithubRepository.Branch("master", "00f99f711380bd6c");
        GithubRepository mockRepository = new GithubRepository(
            "john",
            "repofinder",
            List.of(mockBranch),
            false);

        given(githubService.findAllNonForkReposFor("john"))
            .willReturn(List.of(mockRepository));

        mockMvc.perform(get("/john")
            .accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ownerLogin").value("john"))
            .andExpect(jsonPath("$[0].name").value("repofinder"))
            .andExpect(jsonPath("$[0].branches[0].name").value("master"))
            .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("00f99f711380bd6c"));

        verify(githubService).findAllNonForkReposFor("john");
    }

}
