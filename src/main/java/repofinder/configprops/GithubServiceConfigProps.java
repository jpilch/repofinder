package repofinder.configprops;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties("application.github-service.config")
public class GithubServiceConfigProps {
    private int reposPerPage;
    private int branchesPerPage;
}
