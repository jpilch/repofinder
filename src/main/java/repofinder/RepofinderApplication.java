package repofinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import repofinder.configurationproperties.GithubClientConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RepofinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepofinderApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public RestTemplate githubClient(
		RestTemplateBuilder restTemplateBuilder,
		GithubClientConfigurationProperties props
	) {
		return restTemplateBuilder
			.defaultHeader("Accept", "application/vnd.github+json")
			.defaultHeader("X-GitHub-Api-Version", props.getApiVersion())
			.defaultHeader("Authorization", "Bearer " + props.getAuthToken())
			.rootUri(props.getRootUri())
			.build();
	}
}
