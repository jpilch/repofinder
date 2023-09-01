package repofinder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusWithMessage {
    @JsonProperty("Message")
    private Object message;
    private int status;
}

