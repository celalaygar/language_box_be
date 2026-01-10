package com.game.find.word.auth.service;

import com.game.find.word.auth.dto.*;
import com.game.find.word.auth.model.Role;
import com.game.find.word.auth.model.User;
import com.game.find.word.auth.repository.UserRepository;
import com.game.find.word.auth.security.JwtProvider;
import com.game.find.word.base.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public User register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthdate(request.getBirthdate());
        user.setRole(Role.USER);
        user = userRepository.save(user);
        return user;
    }



    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }


    @Override
    public List<UserDto> findAll() {
        List<User> list = userRepository.findAll();
        List<UserDto> listDto = list.stream().map(user -> new UserDto(user)).collect(Collectors.toList());
        return listDto;
    }


    @Override
    public User findUserByAuthentication(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty())
            return null;
        return userOpt.get();
    }
    @Override
    public BaseResponse<UserDto> updateMe(UserUpdateDto request){
        User user = findUserByAuthentication();
        user.setBirthdate(request.getBirthdate());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        //user.setEmail(request.getEmail());
        userRepository.save(user);
        return new BaseResponse<UserDto>(true, HttpStatus.OK,new UserDto(user),
                UserResponseStatus.OK.name(),"User updated");
    }

    @Override
    public BaseResponse<Boolean> changePassword(ResetPasswordRequestDto request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            return new BaseResponse<Boolean>(true, HttpStatus.OK,false,
                    UserResponseStatus.NOT_FOUND.name(),"User not found");
        }

        if(!request.getNewPassword().equals(request.getConfirmNewPassword())){
            return new BaseResponse<Boolean>(true, HttpStatus.OK,false,
                    UserResponseStatus.NOT_MATCHED.name(),"Not matched passwords");
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new BaseResponse<Boolean>(true, HttpStatus.OK,true,
                UserResponseStatus.OK.name(),"Password changed ");
    }

    @Override
    public BaseResponse<UserDto> getMe() {
        User user = findUserByAuthentication();
        return new BaseResponse<UserDto>(true, HttpStatus.OK,new UserDto(user),
                UserResponseStatus.OK.name(),"User Found");
    }
}