package project.scanny.configuration.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import project.scanny.models.User;

import java.util.List;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final User user;

    public AuthenticationToken(User user, GrantedAuthority authority) {
        super(List.of(authority));
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
