package repofinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import repofinder.service.GithubService;

import java.util.List;

import static org.mockito.BDDMockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
		given(githubService.findAllNonForkReposFor("john")).willReturn(List.of());

		testRestTemplate.getForObject("/john", String.class);
		testRestTemplate.getForObject("/john", String.class);

		verify(githubService, times(1)).findAllNonForkReposFor(anyString());
	}

	@Test
	void identifiesCachedResultsByUsername() throws Exception {
		given(githubService.findAllNonForkReposFor("john")).willReturn(List.of());

		testRestTemplate.getForObject("/bob", String.class);
		testRestTemplate.getForObject("/alice", String.class);

		verify(githubService, times(2)).findAllNonForkReposFor(anyString());
	}
}
