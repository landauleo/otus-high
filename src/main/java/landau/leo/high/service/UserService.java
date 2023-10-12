package landau.leo.high.service;

import java.util.UUID;

import landau.leo.high.dao.UserDao;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public String authenticateUser(LoginUserRequest dto) {

        String hashedPasswordFromDb = userDao.getById(dto.getId()).getPassword();

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
        return GetUserResponse.toDto(userDao.getById(userId));
    }

}
