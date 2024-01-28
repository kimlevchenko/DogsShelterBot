package pro.sky.telegrambot.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.UserRepository;

import java.util.List;

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
