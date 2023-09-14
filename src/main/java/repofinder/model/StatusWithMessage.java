package repofinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusWithMessage {
    private Object message;
    private int status;
}

