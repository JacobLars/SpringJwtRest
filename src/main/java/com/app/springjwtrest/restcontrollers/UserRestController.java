package com.app.springjwtrest.restcontrollers;

import com.app.springjwtrest.models.AuthenticationRequest;
import com.app.springjwtrest.models.AuthenticationResponse;
import com.app.springjwtrest.utils.JwtUtil;
import com.app.springjwtrest.services.MyUserDetailsService;
import com.app.springjwtrest.models.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private MyUserDetailsService userService;

    private final Pbkdf2PasswordEncoder pe = new Pbkdf2PasswordEncoder();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    
    @GetMapping("/users/get/all")
    public List<UserEntity> getAllUsers() {

        return userService.getAllUsers();
    }

  
    @PostMapping("/users/register")
    public ResponseEntity<String> addUser(@RequestBody UserEntity user) {
        if (user.getPassword().isBlank()) {
            return ResponseEntity.accepted().body("Enter a password!");
        }
        user.setPassword(pe.encode(user.getPassword()));
        

        List<UserEntity> savedUsers = userService.getAllUsers();

        for (UserEntity savedUser : savedUsers) {

            if (savedUser.getUsername().equals(user.getUsername())) {
                return ResponseEntity.accepted().body("Username " + user.getUsername() + " is already taken.");
            }
        }

        userService.SaveUser(user);

        return ResponseEntity.accepted().body("User has been added to database!");
    }

  
 
    
    @GetMapping("/users/login")
    public AuthenticationResponse userLogin(@RequestBody AuthenticationRequest authRequestModel) throws Exception {

        UserDetails details = userService.loadUserByUsername(authRequestModel.getUsername());

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestModel.getUsername(),
                            details.getPassword()));

        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password...", e);
        }
        final String jwt = jwtUtil.getJWT(details);
        Optional<UserEntity> theUser = userService.findUser(authRequestModel.getUsername());
        if (pe.matches(authRequestModel.getPassword(), theUser.get().getPassword()) == true
                && theUser.get().getUsername().equals(authRequestModel.getUsername())) {
        

            return new AuthenticationResponse(jwt);
        }
        return null;
    }


    @PostMapping("/users/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.accepted().body("User with username: " + username + " was deleted.");
    }

    
    @PostMapping("/users/edit/password/{username}/{newPassword}")
    public ResponseEntity<String> changeUserPassword(@PathVariable("username") String username,
            @PathVariable("newPassword") String password) {

        UserEntity user = userService.findUser(username).get();
        user.setPassword(pe.encode(password));
        userService.SaveUser(user);
        return ResponseEntity.accepted().body("Password has been changed!");
    }

}
