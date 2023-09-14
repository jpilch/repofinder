package repofinder.model.api;

public record GithubBranch(String name, Commit commit) {

    public record Commit(String sha) {}
}
