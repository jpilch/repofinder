package repofinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import repofinder.json.RepositoryBranchJsonComponent;
import repofinder.json.RepositoryJsonComponent;
import repofinder.model.GithubRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GithubRepositoryJsonTests {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule()
            .addDeserializer(GithubRepository.class, new RepositoryJsonComponent.Deserializer())
            .addDeserializer(GithubRepository.Branch.class, new RepositoryBranchJsonComponent.Deserializer());
        this.mapper.registerModule(module);

    }

    @Test
    public void deserializeRepo() throws Exception {
        String jsonString = "{\"name\": \"repofinder\",\"owner\": {\"login\": \"john\"},\"fork\": false}";

        GithubRepository repo = mapper.readValue(jsonString, GithubRepository.class);

        assertEquals(repo.getName(), "repofinder");
        assertEquals(repo.getOwnerLogin(), "john");
        assertEquals(repo.getBranches().size(), 0);
        assertFalse(repo.isFork());
    }

    @Test
    public void serializeRepo() throws Exception {
        GithubRepository repo = new GithubRepository("john", "repofinder", List.of(), false);

        String serializedRepo = mapper.writeValueAsString(repo);
        String expectedString = "{\"ownerLogin\":\"john\",\"name\":\"repofinder\",\"branches\":[]}";

        assertEquals(serializedRepo, expectedString);
    }
}
