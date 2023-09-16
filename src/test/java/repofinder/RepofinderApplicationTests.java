package repofinder;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepofinderApplicationTests {

	@RegisterExtension
	static WireMockExtension mockServer = WireMockExtension.newInstance()
			.options(wireMockConfig().dynamicPort().dynamicHttpsPort())
			.build();

	@DynamicPropertySource
	static void configureMockServer(DynamicPropertyRegistry registry) {
		registry.add("github.client.config.rootUri", mockServer::baseUrl);
	}

	@Test
	void contextLoads() {
	}

	@Test
	@Disabled
	public void shouldGetRepositoriesForGivenUsername() throws Exception {

	}
}
