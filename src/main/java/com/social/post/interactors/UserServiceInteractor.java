package com.social.post.interactors;

import com.social.post.dtos.UserProfileResponseDto;
import com.social.post.exception.UserProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceInteractor {

    private final RestTemplate restTemplate;

    @Value("${application.user-service.url}")
    private String userServiceBaseUrl;

    public UserProfileResponseDto getUserProfile(long userId){
        UserProfileResponseDto userProfileResponseDto = null;
        String uri = userServiceBaseUrl+"/getProfile?userId="+userId;
        try {
            log.info("calling user UserServiceInteract");
            userProfileResponseDto = restTemplate.getForObject(uri, UserProfileResponseDto.class);
        }catch (UserProfileNotFoundException exception){
            log.error("Error while fetching data from user service");
            throw new UserProfileNotFoundException("Error fetching user profile "+userId, exception);
        }
        return userProfileResponseDto;
    }
}
