package repofinder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class GithubRepository implements Serializable {
    private String ownerLogin;
    private String name;
    private List<Branch> branches;

    @JsonIgnore
    private boolean fork;

    @JsonIgnore
    public boolean isNotAFork() {
        return !this.fork;
    }

    @Data
    @AllArgsConstructor
    public static class Branch implements Serializable {
        private String name;
        private String lastCommitSha;
    }
}
