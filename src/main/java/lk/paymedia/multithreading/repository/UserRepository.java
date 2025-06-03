package lk.paymedia.multithreading.repository;

import lk.paymedia.multithreading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}