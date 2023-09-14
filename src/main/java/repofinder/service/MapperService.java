package repofinder.service;

import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.List;

public interface MapperService {
    public Repository map(GithubRepository githubRepository, List<GithubBranch> githubBranches);
}
