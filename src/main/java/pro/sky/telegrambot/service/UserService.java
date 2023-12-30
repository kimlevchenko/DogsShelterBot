package pro.sky.telegrambot.service;


import com.pengrad.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import pro.sky.telegrambot.repositories.UserRepository;

import java.util.List;

;

@Service
public class UserService {
    private final UserRepository UserRepository;

    public UserService(UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }

    /**
     * Метод возвращает всех пользователей
     * Используется метод репозитория {@link JpaRepository#findAll()}
     *
     * @return Список всех пользователей
     */
    public List<User> getAllUsers() {
        return List.copyOf(UserRepository.findAll());
    }
}
