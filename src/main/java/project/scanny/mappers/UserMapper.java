package project.scanny.mappers;

import project.scanny.dto.UserDTO;
import project.scanny.models.User;
import project.scanny.requests.user.CreateUserRequest;

public class UserMapper {
    public static UserDTO userToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername());
    }

    public static User createRequestToUser(CreateUserRequest createUserRequest) {
        return new User(createUserRequest.username(), createUserRequest.password());
    }
}
