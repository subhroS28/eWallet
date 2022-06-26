package com.subhro.eWallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhro.eWallet.model.User;
import com.subhro.eWallet.repository.UserRepository;
import com.subhro.eWallet.request.UserCreateRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import static com.subhro.eWallet.CommonConstants.USER_CREATE_KAFKA_TOPIC;
import static com.subhro.eWallet.CommonConstants.EMAIL_ATTRIBUTE;
import static com.subhro.eWallet.CommonConstants.PHONE_ATTRIBUTE;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    ObjectMapper objectMapper;

    public User getUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public User getUserByPhoneNumber(int number){
        return repository.findByPhoneNumber(number);
    }

    public User getUserById(int ID){
        return repository.findById(ID).orElse(null);
    }

    public void createNewUser(UserCreateRequest userCreateRequest) throws JsonProcessingException {
        User user = userCreateRequest.toUser();
        repository.save(user);

        //Produce Kafka
        JSONObject userRequest = new JSONObject();
        userRequest.put(EMAIL_ATTRIBUTE, user.getEmail());
        userRequest.put(PHONE_ATTRIBUTE, user.getPhoneNumber());

        String userCreateRequestInString = objectMapper.writeValueAsString(userRequest);
        kafkaTemplate.send(USER_CREATE_KAFKA_TOPIC, userCreateRequestInString);
    }
}
