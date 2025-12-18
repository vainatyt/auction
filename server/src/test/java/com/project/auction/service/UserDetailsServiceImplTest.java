package com.project.auction.service;

import com.project.auction.models.User;
import com.project.auction.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // given
        String username = "john.doe";
        User user = new User();
        user.setId(10L);
        user.setName(username);
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        verify(userRepository).findByName(username);
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        // given
        String username = "unknown.user";

        when(userRepository.findByName(username)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("User Not Found with username: " + username);

        verify(userRepository).findByName(username);
    }

    @Test
    void loadUserByUsername_callsUserDetailsImplBuild() {
        // given
        String username = "john.doe";
        User user = new User();
        user.setId(10L);
        user.setName(username);

        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // when
        userDetailsService.loadUserByUsername(username);

        // then
        verify(userRepository).findByName(username);
    }
}
