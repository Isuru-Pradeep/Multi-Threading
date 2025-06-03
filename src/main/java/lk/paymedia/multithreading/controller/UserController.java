package lk.paymedia.multithreading.controller;

import lk.paymedia.multithreading.entity.User;
import lk.paymedia.multithreading.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces= "application/json")
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            userService.saveUsers(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users")
    public CompletableFuture<ResponseEntity<List<User>>> getUsers() {
        return userService.loadUsers()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/getUsersByMultipuleThreads")
    public ResponseEntity<?> getUsersMultipulThreads() {
        try {
            CompletableFuture<List<User>> users1 = userService.loadUsers();
            CompletableFuture<List<User>> users2 = userService.loadUsers();
            CompletableFuture<List<User>> users3 = userService.loadUsers();

            CompletableFuture.allOf(users1, users2, users3).join();

            List<User> combinedUsers = new ArrayList<>();
            combinedUsers.addAll(users1.join());
            combinedUsers.addAll(users2.join());
            combinedUsers.addAll(users3.join());

            return ResponseEntity.ok(combinedUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load users: " + e.getMessage());
        }
    }

}
