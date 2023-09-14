package repofinder.model.api;

public record GithubRepository(String name, Owner owner, boolean fork) {

    public boolean isNotAFork() {
        return !fork;
    }

    public record Owner(String login) {}
}
