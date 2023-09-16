package repofinder;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repofinder.model.Repository;
import repofinder.model.api.GithubBranch;
import repofinder.model.api.GithubRepository;
import repofinder.service.MapperService;
import repofinder.service.MapperServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperServiceImplTests {

    private MapperService mapperService;

    @BeforeEach
    public void setUp() {
        mapperService = new MapperServiceImpl();
    }

    @Test
    public void mapsGithubBranch() throws Exception {
        GithubBranch mockBranch = new GithubBranch("master", new GithubBranch.Commit("ef9a01f1"));

        Repository.Branch result = mapperService.mapBranch(mockBranch);

        assertEquals(result.name(), mockBranch.name());
        assertEquals(result.lastCommitSha(), mockBranch.commit().sha());
    }

    @Test
    public void mapsGithubRepository() throws Exception {
        GithubRepository mockRepo = new GithubRepository(
                "repofinder",
                new GithubRepository.Owner("john"),
                false
        );
        List<Repository.Branch> preparedBranches = List.of(
                new Repository.Branch("master", "8d88330f")
        );

        Repository result = mapperService.mapRepository(mockRepo, preparedBranches);

        assertEquals(result.name(), mockRepo.name());
        assertEquals(result.ownerLogin(), mockRepo.owner().login());
        assertEquals(result.branches().size(), preparedBranches.size());
        assertEquals(result.branches().get(0), preparedBranches.get(0));
    }
}
