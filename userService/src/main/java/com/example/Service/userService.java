package com.example.Service;

import com.example.CommonConstants;
import com.example.Model.user;
import com.example.Repositories.userRepository;
import com.example.RequestDtos.userCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Enums.userConstants;

import java.util.List;

@Service
public class userService implements UserDetailsService {

    @Autowired
    userRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;
    public void create(userCreateRequest userCreateRequest) throws JsonProcessingException {
        user user=userCreateRequest.toUser();
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setAuthorities(userConstants.USER_AUTHORITY);
        userRepository.save(user);

        // publish the event post user creation which eill be listened by the consumers

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USERID,user.getId());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER,user.getPhoneNo());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE,user.getIdentifierValue());
        jsonObject.put(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_KEY,user.getUserIdentifier());

        kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC,objectMapper.writeValueAsString(jsonObject));
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        return userRepository.findByPhoneNo(phoneNo);
    }

    public List<user> getAll() {
        return userRepository.findAll();
    }
}
