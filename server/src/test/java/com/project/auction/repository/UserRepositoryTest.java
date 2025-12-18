package com.project.auction.repository;

import com.project.auction.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByName_and_existsByName_workCorrectly() {
        User user = new User("testUser", "pwd", "test@test.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByName("testUser");
        boolean exists = userRepository.existsByName("testUser");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
        assertThat(exists).isTrue();
    }

    @Test
    void findByEmail_and_existsByEmail_workCorrectly() {
        User user = new User("mailUser", "pwd", "mail@test.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("mail@test.com");
        boolean exists = userRepository.existsByEmail("mail@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("mailUser");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_returnsFalse_forUnknownUser() {
        assertThat(userRepository.existsByName("unknown")).isFalse();
    }

    @Test
    void existsByEmail_returnsFalse_forUnknownEmail() {
        assertThat(userRepository.existsByEmail("unknown@test.com")).isFalse();
    }
}
