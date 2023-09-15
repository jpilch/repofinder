package repofinder.service;

import org.springframework.stereotype.Service;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;

import java.util.List;

@Service
public class MapperServiceImpl implements MapperService {

    @Override
    public Repository mapRepository(GithubRepository githubRepository, List<Repository.Branch> branches) {
        return new Repository(githubRepository.owner().login(), githubRepository.name(), branches);
    }

    @Override
    public Repository.Branch mapBranch(GithubBranch githubBranch) {
        return new Repository.Branch(githubBranch.name(), githubBranch.commit().sha());
    }
}
