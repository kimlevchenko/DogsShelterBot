package pro.sky.telegrambot.repositories;


import com.pengrad.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
