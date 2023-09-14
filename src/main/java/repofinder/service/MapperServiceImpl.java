package repofinder.service;

import org.springframework.stereotype.Service;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.List;

@Service
public class MapperServiceImpl implements MapperService {

    @Override
    public Repository map(GithubRepository githubRepository, List<GithubBranch> githubBranches) {
        String ownerLogin = githubRepository.owner().login();
        String repositoryName = githubRepository.name();
        List<Repository.Branch> mappedBranches = githubBranches
                .stream()
                .map(this::mapBranch)
                .toList();

        return new Repository(ownerLogin, repositoryName, mappedBranches);
    }

    private Repository.Branch mapBranch(GithubBranch githubBranch) {
        String branchName = githubBranch.name();
        String lastCommitSha = githubBranch.commit().sha();

        return new Repository.Branch(branchName, lastCommitSha);
    }
}
