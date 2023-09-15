package repofinder.service;

import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.List;

public interface MapperService {
    Repository mapRepository(GithubRepository githubRepository, List<Repository.Branch> branches);

    Repository.Branch mapBranch(GithubBranch githubBranch);
}
