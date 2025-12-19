package com.project.auction.service;

import com.project.auction.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {

    @Mock
    private UserDetails userDetails;

    @Test
    void build_createsUserDetails_fromUser() {
        // given
        User user = new User();
        user.setId(10L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        // when
        UserDetailsImpl userDetailsImpl = UserDetailsImpl.build(user);

        // then
        assertThat(userDetailsImpl.getId()).isEqualTo(10L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("John Doe");
        assertThat(userDetailsImpl.getEmail()).isEqualTo("john@example.com");
        assertThat(userDetailsImpl.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetailsImpl.getAuthorities()).isEmpty();
    }

    @Test
    void constructor_setsAllFields() {
        // given
        Long id = 10L;
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";
        
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // when
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(id, name, email, password, authorities);

        // then
        assertThat(userDetailsImpl.getId()).isEqualTo(id);
        assertThat(userDetailsImpl.getUsername()).isEqualTo(name);
        assertThat(userDetailsImpl.getEmail()).isEqualTo(email);
        assertThat(userDetailsImpl.getPassword()).isEqualTo(password);
        assertThat(userDetailsImpl.getAuthorities()).hasSize(authorities.size())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void userDetailsMethods_returnExpectedValues() {
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());

        assertThat(userDetailsImpl.isAccountNonExpired()).isTrue();
        assertThat(userDetailsImpl.isAccountNonLocked()).isTrue();
        assertThat(userDetailsImpl.isCredentialsNonExpired()).isTrue();
        assertThat(userDetailsImpl.isEnabled()).isTrue();
    }

    @Test
    void equals_sameId_returnsTrue() {
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(10L, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(20L, "John", "john@test.com", "pass", Collections.emptyList());

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equals_nullId_sameNull_returnsTrue() {
        UserDetailsImpl user1 = new UserDetailsImpl(null, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(null, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        String other = "not a UserDetailsImpl";

        assertThat(userDetailsImpl).isNotEqualTo(other);
    }

    @Test
    void hashCode_sameId_sameHash() {
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(10L, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
