package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
