package project.scanny.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.scanny.configuration.auth.JwtTokenUtil;
import project.scanny.dto.AuthenticationResponse;
import project.scanny.dto.ChangePasswordRequest;
import project.scanny.mappers.UserMapper;
import project.scanny.models.User;
import project.scanny.requests.user.CreateUserRequest;
import project.scanny.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(UserService userService,
                          JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping
    public AuthenticationResponse createOrLoginUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        User user = UserMapper.createRequestToUser(createUserRequest);
        try {
            User loggedInUser = userService.createOrLoginUser(user);
            String token = jwtTokenUtil.generateToken(loggedInUser.getUsername());
            return new AuthenticationResponse(token, loggedInUser.getId());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            userService.changePassword(username, request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
