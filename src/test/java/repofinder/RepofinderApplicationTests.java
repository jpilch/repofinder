package repofinder;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import repofinder.model.Repository;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"test", "integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepofinderApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@RegisterExtension
	static WireMockExtension mockServer = WireMockExtension.newInstance()
			.options(wireMockConfig().dynamicPort())
			.build();

	@DynamicPropertySource
	static void configureMockServer(DynamicPropertyRegistry registry) {
		registry.add("github.client.config.rootUri", mockServer::baseUrl);
	}

	@Test
	void contextLoads() {}

	@Test
	public void shouldGetRepositoriesForGivenUsername() throws Exception {
		String mockRepoString = "[{\"name\":\"repofinder\",\"fork\":false,\"owner\":{\"login\":\"jpilch\"}}]";
		String mockBranchesString = "[{\"name\":\"master\",\"commit\":{\"sha\":\"8e2eac98\"}}]";

		mockServer.stubFor(get("/users/jpilch/repos?type=all&page=1&per_page=100")
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(mockRepoString)));

		mockServer.stubFor(get("/repos/jpilch/repofinder/branches?page=1&per_page=100")
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(mockBranchesString)));

		Repository[] repositories = restTemplate.getForObject("/jpilch", Repository[].class);

		assertEquals(repositories.length, 1);
		assertEquals(repositories[0].name(), "repofinder");
		assertEquals(repositories[0].ownerLogin(), "jpilch");
		assertEquals(repositories[0].branches().size(), 1);
		assertEquals(repositories[0].branches().get(0).name(), "master");
		assertEquals(repositories[0].branches().get(0).lastCommitSha(), "8e2eac98");
	}
}
