package ru.javawebinar.topjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.javawebinar.topjava.AuthUser;
import ru.javawebinar.topjava.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, UserDetailsService {
    Optional<User> findByName(String name);

    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User named '%s' could not found!", username)));
        return new AuthUser(user);
    }
}
