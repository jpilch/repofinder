package repofinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient(GithubClientConfigProps clientConfig) {
        return WebClient.builder()
                .baseUrl(clientConfig.getRootUri())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", clientConfig.getApiVersion())
                .defaultHeader("Authorization", "Bearer " + clientConfig.getAuthToken())
                .build();
    }
}
