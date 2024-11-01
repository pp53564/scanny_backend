package project.scanny.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.scanny.dto.UserDTO;
import project.scanny.mappers.UserMapper;
import project.scanny.models.User;
import project.scanny.requests.user.CreateUserRequest;
import project.scanny.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping
//    public UserDTO createOrLoginUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
//       return UserMapper.userToDTO(userService.createUser(UserMapper.createRequestToUser(createUserRequest)));
//    }

    @PostMapping
    public UserDTO createOrLoginUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        User user = UserMapper.createRequestToUser(createUserRequest);
        User loggedInUser = userService.createOrLoginUser(user);
        return UserMapper.userToDTO(loggedInUser);
    }


}
