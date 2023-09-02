package repofinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import repofinder.model.GithubRepository;
import repofinder.service.GithubService;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepofinderApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	RedisCacheManager cacheManager;

	@MockBean
	GithubService githubService;

	@BeforeEach
	void setUp() {
		Cache cache = cacheManager.getCache("repos");
		if (cache != null) {
			cache.clear();
		}
	}

	@Test
	void contextLoads() {
	}

	@Test
	void cachesResults() throws Exception {
		GithubRepository repo = new GithubRepository("john", "repofinder", List.of(), false);
		given(githubService.findAllNonForkReposFor("john")).willReturn(List.of(repo));

		String serializedRepo1 = testRestTemplate.getForObject("/john", String.class);
		String serializedRepo2 = testRestTemplate.getForObject("/john", String.class);

		verify(githubService, times(1)).findAllNonForkReposFor(anyString());
		assertEquals(serializedRepo1, serializedRepo2);
	}

	@Test
	void identifiesCachedResultsByUsername() throws Exception {
		GithubRepository repo1 = new GithubRepository("bob", "test", List.of(), false);
		GithubRepository repo2 = new GithubRepository("alice", "test", List.of(), false);
		given(githubService.findAllNonForkReposFor("bob"))
			.willReturn(List.of(repo1));
		given(githubService.findAllNonForkReposFor("alice"))
			.willReturn(List.of(repo2));

		String serializedRepo1 = testRestTemplate.getForObject("/bob", String.class);
		String serializedRepo2 = testRestTemplate.getForObject("/alice", String.class);

		verify(githubService, times(2)).findAllNonForkReposFor(anyString());
		assertNotEquals(serializedRepo1, serializedRepo2);
	}
}
