package repofinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GithubRepository {
    private String ownerLogin;
    private String name;
    @JsonIgnore
    public boolean fork;
    private List<Branch> branches;

    @JsonIgnore
    public boolean isNotAFork() {
        return !this.fork;
    }

    @Data
    @AllArgsConstructor
    public static class Branch {
        private String name;
        private String commitSha;
    }
}
