package repofinder.jsoncomponents;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonComponent;
import repofinder.model.GithubRepository;

import java.io.IOException;
import java.util.List;

@JsonComponent
public class RepositoryJsonComponent {

    public static class Deserializer extends JsonDeserializer<GithubRepository> {

        @Override
        public GithubRepository deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode tree = codec.readTree(jsonParser);
            String ownerLogin = tree.get("owner").get("login").textValue();
            String name = tree.get("name").textValue();
            boolean fork = tree.get("fork").asBoolean();
            return new GithubRepository(ownerLogin, name, fork, List.of());
        }
    }
}
