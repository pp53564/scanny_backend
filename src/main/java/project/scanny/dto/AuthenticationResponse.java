package project.scanny.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {
    private String token;
    private Long id;

    public AuthenticationResponse(String token, Long id) {
        this.token = token;
        this.id = id;
    }
}