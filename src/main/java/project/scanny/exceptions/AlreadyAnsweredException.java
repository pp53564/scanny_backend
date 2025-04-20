package project.scanny.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyAnsweredException extends RuntimeException {
    public AlreadyAnsweredException(String message) {
        super(message);
    }
}

