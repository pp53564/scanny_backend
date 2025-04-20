package project.scanny.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyImageException extends RuntimeException {
    public EmptyImageException(String message) {
        super(message);
    }
}

