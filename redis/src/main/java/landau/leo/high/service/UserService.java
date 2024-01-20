package landau.leo.high.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import landau.leo.high.dao.DialogMessageRepository;
import landau.leo.high.dao.PostRepository;
import landau.leo.high.dao.UserRepository;
import landau.leo.high.dto.DialogMessage;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.dto.GetUserShortInfoResponse;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.PostResponse;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.entity.DialogMessageEntity;
import landau.leo.high.entity.PostEntity;
import landau.leo.high.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DialogMessageRepository dialogMessageRepository;
    private final PostRepository postRepository;

    public String authenticateUser(LoginUserRequest dto) {

        String hashedPasswordFromDb = userRepository.findById(UUID.fromString(dto.getId())).get().getPassword();

        boolean isPasswordCorrect = BCrypt.checkpw(dto.getPassword(), hashedPasswordFromDb);
        if (!isPasswordCorrect) {
            throw new BadCredentialsException("Invalid password");
        }
        return UUID.randomUUID().toString();
    }

    public String registerUser(RegisterUserRequest dto) {
        UUID uuid = UUID.randomUUID();

        UserEntity user = UserEntity.builder().id(uuid).firstName(dto.getFirstName()).secondName(dto.getSecondName()).birthdate(dto.getBirthdate()).biography(dto.getBiography()).city(dto.getCity()).password(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt())).build();

        userRepository.save(user);
        return uuid.toString();
    }

    public GetUserResponse getUserById(UUID userId) {
        return GetUserResponse.toDto(Objects.requireNonNull(userRepository.findById(userId).orElse(null)));
    }

    public List<GetUserShortInfoResponse> getUserByFirstAndSecondName(String firstName, String secondName) {
        return userRepository.findAllByFirstNameAndSecondName(firstName, secondName).stream().map(GetUserShortInfoResponse::toDto).collect(Collectors.toList());
    }

    public Integer countUsers() {
        return Math.toIntExact(userRepository.count());
    }

    public void loadDefaultUsers() {
        new File("people.csv");
        InputStream is = getClass().getClassLoader().getResourceAsStream("people.csv");
        BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);

        userRepository.deleteAll();

        Scanner s = new Scanner(bis);
        String firstName = "";
        String secondName = "";
        int pos = 0;

        log.info("Is about to insert default users");

        List<UserEntity> users = new ArrayList<>();

        try {
            while (s.hasNextLine()) {
                secondName = s.next().trim().toLowerCase(Locale.ROOT);
                firstName = s.nextLine().split(",")[0].trim().toLowerCase(Locale.ROOT);
                users.add(new UserEntity(UUID.randomUUID(), firstName, secondName));
                pos++;
                if (pos % 1000 == 0) log.info(pos + " rows to be inserted");
            }
            log.info(pos + " rows are gonna be batch-inserted");
            userRepository.saveAll(users);

            log.info(pos + " were successfully batch-inserted");
        } catch (Exception e) {
            log.error("firstName: " + firstName + " secondName: " + secondName);
            log.error("failed at row " + pos, e);
            throw e;
        }
        log.info("Transaction is committed and closed");

    }

    @SneakyThrows
    public void loadDefaultPosts() {
        new File("posts.txt");
        InputStream is = getClass().getClassLoader().getResourceAsStream("posts.txt");
        BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);

        postRepository.deleteAll();

        Scanner s = new Scanner(bis);
        int pos = 0;
        List<PostEntity> posts = new ArrayList<>();
        log.info("Is about to insert default posts");
        LocalDateTime parseStart = LocalDateTime.now();
        try {

            while (s.hasNextLine()) {
                posts.add(new PostEntity(UUID.randomUUID(), UUID.randomUUID(), s.nextLine()));
                pos++;
                if (pos % 1000 == 0) log.info(pos + " rows to be inserted");
            }
            log.info(pos + " rows are gonna be batch-inserted");
            postRepository.saveAll(posts);

            log.info(pos + " were successfully batch-inserted");
        } catch (Exception e) {
            log.error("failed at row " + pos, e);
            throw e;
        }
        log.info("Transaction is committed and closed");

        LocalDateTime parseEnd = LocalDateTime.now();
        log.info("Duration of insert default posts: {} seconds", Duration.between(parseStart, parseEnd).getSeconds());
    }

    @Cacheable("usersPosts")
    public List<PostResponse> getUsersPosts(int offset, long limit) {
        return postRepository.findAllWithOffSetAndLimit(offset, limit).stream().map(PostResponse::toDto).collect(Collectors.toList());
    }

    public List<DialogMessage> getUsersDialogs(String userId) {
        return dialogMessageRepository.findAllByFrom(UUID.fromString(userId)).stream().map(DialogMessage::toDto).collect(Collectors.toList());
    }

    public void send(UUID fromUserId, UUID toUserId, String text) {
        dialogMessageRepository.save(new DialogMessageEntity(fromUserId, toUserId, text));
    }

}
