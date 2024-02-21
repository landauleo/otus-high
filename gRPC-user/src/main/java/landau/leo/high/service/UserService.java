package landau.leo.high.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import landau.leo.high.dao.PostDao;
import landau.leo.high.dao.UserDao;
import landau.leo.high.dto.DialogMessageResponse;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.dto.GetUserShortInfoResponse;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.PostResponse;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final PostDao postDao;
    private final DialogueGrpcClient dialogueGrpcClient;

    public String authenticateUser(LoginUserRequest dto) {

        String hashedPasswordFromDb = userDao.getUserById(dto.getId()).getPassword();

        boolean isPasswordCorrect = BCrypt.checkpw(dto.getPassword(), hashedPasswordFromDb);
        if (!isPasswordCorrect) {
            throw new BadCredentialsException("Invalid password");
        }
        return UUID.randomUUID().toString();
    }

    public String registerUser(RegisterUserRequest dto) {
        UUID uuid = UUID.randomUUID();

        UserEntity user = UserEntity.builder()
                .id(uuid)
                .firstName(dto.getFirstName())
                .secondName(dto.getSecondName())
                .birthdate(dto.getBirthdate())
                .biography(dto.getBiography())
                .city(dto.getCity())
                .password(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()))
                .build();

        userDao.insert(user);
        return uuid.toString();
    }

    public GetUserResponse getUserById(String userId) {
        return GetUserResponse.toDto(userDao.getUserById(userId));
    }

    public List<GetUserShortInfoResponse> getUserByFirstAndSecondName(String firstName, String secondName) {
        return userDao.getByFirstAndSecondName(firstName, secondName).stream().map(GetUserShortInfoResponse::toDto).collect(Collectors.toList());
    }

    public Integer countUsers() {
        return userDao.getUsersNumber();
    }

    public void loadDefaultUsers() {
        userDao.loadDefaultUsers();
    }

    public void loadDefaultPosts() {
        postDao.loadDefaultPosts();
    }

    @Cacheable("usersPosts")
    public List<PostResponse> getUsersPosts(int offset, long limit) {
        return postDao.getFriendsPosts(offset, limit).stream().map(PostResponse::toDto).collect(Collectors.toList());
    }

    public List<DialogMessageResponse> getUsersDialogs(String userId) {
        return dialogueGrpcClient.getUsersDialogs(userId).stream().map(DialogMessageResponse::toDto).collect(Collectors.toList());
    }

    public void send(UUID fromUserId, UUID toUserId, String text) {
        dialogueGrpcClient.sendUserMessage(fromUserId, toUserId, text);
    }

}
