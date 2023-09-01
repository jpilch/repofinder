package repofinder.jsoncomponents;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;
import repofinder.model.GithubRepository;

import java.io.IOException;

@JsonComponent
public class RepositoryBranchJsonComponent {

    public static class Deserializer extends JsonDeserializer<GithubRepository.Branch> {

        @Override
        public GithubRepository.Branch deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode tree = codec.readTree(jsonParser);
            String name = tree.get("name").textValue();
            String commitSha = tree.get("commit").get("sha").textValue();
            return new GithubRepository.Branch(name, commitSha);
        }
    }
}