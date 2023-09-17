package repofinder.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import repofinder.model.Repository;
import repofinder.service.GithubService;

import java.util.List;

@RestController
public class RepofinderController {
    private final GithubService githubService;
    private static Logger logger = LoggerFactory.getLogger(RepofinderController.class);

    public RepofinderController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public Flux<Repository> findAllNonForkReposFor(@PathVariable String username) {
        logger.info("Finding non fork repositories for {}", username);
        return githubService.findAllNonForkReposFor(username);
    }
}
