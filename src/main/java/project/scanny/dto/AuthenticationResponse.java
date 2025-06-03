package project.scanny.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {
    private String token;
    private Long id;
    private final String role;

    public AuthenticationResponse(String token, Long id, String role) {
        this.token = token;
        this.id = id;
        this.role = role;
    }
}