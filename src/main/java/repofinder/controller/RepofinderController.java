package repofinder.controller;

import org.springframework.web.bind.annotation.*;
import repofinder.model.GithubRepository;
import repofinder.service.GithubService;

import java.util.List;

@RestController
public class RepofinderController {
    private final GithubService githubService;

    public RepofinderController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public List<GithubRepository> listReposFor(@PathVariable String username) {
        return githubService.findAllReposFor(username);
    }
}
