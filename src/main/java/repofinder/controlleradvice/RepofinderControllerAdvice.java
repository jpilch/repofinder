package repofinder.controlleradvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import repofinder.response.StatusWithMessage;

@ControllerAdvice
public class RepofinderControllerAdvice {

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<StatusWithMessage> handleHttpMediaTypeNotAcceptableException(
        HttpMediaTypeNotAcceptableException exception
    ) {
        StatusWithMessage responseBody = new StatusWithMessage(
            exception.getMessage(),
            exception.getStatusCode().value());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new ResponseEntity<>(responseBody, headers, exception.getStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<StatusWithMessage> handleHttpClientErrorException(
        HttpClientErrorException exception
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode errorResponseBody = objectMapper.readTree(exception.getResponseBodyAsString());
        String errorResponseMessage = errorResponseBody.get("message").textValue();
        StatusWithMessage responseBody = new StatusWithMessage(
            errorResponseMessage,
            exception.getStatusCode().value());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new ResponseEntity<>(responseBody, headers, exception.getStatusCode());
    }
}
