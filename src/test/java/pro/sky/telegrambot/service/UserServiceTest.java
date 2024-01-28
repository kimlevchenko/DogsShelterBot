package pro.sky.telegrambot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void getWaitingListTest() {
        User user = new User();
        List<User> users = new ArrayList<>(Collections.singletonList(user));
        when(userRepository.findAll()).thenReturn(users);
        assertThat(userService.getAllUsers()).isEqualTo(users);
        verify(userRepository, times(1)).findAll();
    }
}
