package com.app.springjwtrest.services;

import com.app.springjwtrest.models.UserEntity;
import com.app.springjwtrest.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserEntity> user = repository.findById(username);

        if (user.isPresent()) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

          //  Arrays.asList(user.get().getRoles().split(",")).stream().forEach(authority -> {
                //authorities.add(new SimpleGrantedAuthority(authority));
            //});
            return new User(user.get().getUsername(), user.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User " + username + " doest not exist..");
        }
    }

    
    public void SaveUser(UserEntity user) {
        repository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    
    public void deleteUser(String username) {
        repository.deleteById(username);
    }

  
    public Optional<UserEntity> findUser(String username) {
        return repository.findById(username);
    }
}
