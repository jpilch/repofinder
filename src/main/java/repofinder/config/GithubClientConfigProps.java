package repofinder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("github.client.config")
public class GithubClientConfigProps {
    private String apiVersion;
    private String authToken;
    private String rootUri;
}
