package repofinder.model;

import java.util.List;

public record Repository(String ownerLogin, String name, List<Branch> branches) {

    public record Branch(String name, String lastCommitSha) {}
}

