package com.project.auction.service;

import com.project.auction.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class UserDetailsImplTest {

    @Test
    void build_createsUserDetails_fromUser() {
        // given
        User user = new User();
        user.setId(10L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        // when
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // then
        assertThat(userDetails.getId()).isEqualTo(10L);
        assertThat(userDetails.getUsername()).isEqualTo("John Doe");
        assertThat(userDetails.getEmail()).isEqualTo("john@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).isEmpty();
        assertThat(userDetails.getUsername()).isEqualTo("John Doe");
    }

    @Test
    void constructor_setsAllFields() {
        // given
        Long id = 10L;
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";
        
        // ✅ ИСПРАВЛЕНО: Collection вместо List
        Collection<? extends GrantedAuthority> authorities = 
        (Collection<? extends GrantedAuthority>) Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // when
        UserDetailsImpl userDetails = new UserDetailsImpl(id, name, email, password, authorities);

        // then
        assertThat(userDetails.getId()).isEqualTo(id);
        assertThat(userDetails.getUsername()).isEqualTo(name);
        assertThat(userDetails.getEmail()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).hasSize(authorities.size())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new));
    }


    @Test
    void userDetailsMethods_returnExpectedValues() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());

        // when/then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void equals_sameId_returnsTrue() {
        // given
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(10L, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        // when/then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        // given
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(20L, "John", "john@test.com", "pass", Collections.emptyList());

        // when/then
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void equals_nullId_sameNull_returnsTrue() {
        // given
        UserDetailsImpl user1 = new UserDetailsImpl(null, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(null, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        // when/then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        String other = "not a UserDetailsImpl";

        // when/then
        assertThat(userDetails).isNotEqualTo(other);
    }

    @Test
    void hashCode_sameId_sameHash() {
        // given
        UserDetailsImpl user1 = new UserDetailsImpl(10L, "John", "john@test.com", "pass", Collections.emptyList());
        UserDetailsImpl user2 = new UserDetailsImpl(10L, "Jane", "jane@test.com", "pass2", Collections.emptyList());

        // when/then
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
