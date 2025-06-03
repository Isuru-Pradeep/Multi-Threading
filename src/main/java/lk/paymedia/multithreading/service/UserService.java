package lk.paymedia.multithreading.service;

import lk.paymedia.multithreading.entity.User;
import lk.paymedia.multithreading.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
        Long startTime = System.currentTimeMillis();
        List<User> users = parseCSVFile(file);
        log.info("Saving list of users of size : {}", users.size());
        userRepository.saveAll(users);
        Long endTime = System.currentTimeMillis();
        log.info("Time taken to save users : {}\n", endTime - startTime);
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> loadUsers() {
        Long startTime = System.currentTimeMillis();
        List<User> users = userRepository.findAll();
        Long endTime = System.currentTimeMillis();
        log.info("Loading list of users by {} and Time taken to load users : {}\n", Thread.currentThread().getName(), endTime - startTime);
        return CompletableFuture.completedFuture(users);
    }

    public List<User> parseCSVFile(MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        try{
                final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;

        } catch (Exception e) {
            log.error("Failed to parse CSV file {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
