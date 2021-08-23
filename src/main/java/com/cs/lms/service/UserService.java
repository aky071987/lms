package com.cs.lms.service;

import com.cs.lms.entity.User;
import com.cs.lms.exception.UserNotFoundException;
import com.cs.lms.repository.UserRepository;
import com.cs.lms.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        user.setUserLmsId(Util.generateLmsUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        userRepository.findById(user.getId())
            .orElseThrow(()-> {
                        LOG.error("User not found with ID -> {}", user.getId());
                        throw new UserNotFoundException(String.format("User not found with ID %d", user.getId()));
                    }
            );
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long phone) {
        final User user = userRepository.searchUserByPhoneNumber(String.valueOf(phone));
        if(user == null){
            LOG.error("User not found with phone -> {}", phone);
            throw new UserNotFoundException(String.format("User not found with phone %d", phone));
        }
        userRepository.deleteById(user.getId());
        return true;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User searchByPhoneNumber(Long phone) {
        return userRepository.searchUserByPhoneNumber(String.valueOf(phone));
    }
}
