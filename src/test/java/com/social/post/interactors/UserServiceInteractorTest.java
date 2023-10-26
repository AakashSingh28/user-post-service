package com.social.post.interactors;

import com.social.post.dtos.UserProfileResponseDto;
import com.social.post.exception.UserProfileNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceInteractorTest {

    @Mock
    private RestTemplate restTemplate;

    @Value("${application.user-service.url}")
    private String userServiceBaseUrl;

    @InjectMocks
    private UserServiceInteractor userServiceInteractor;

    @Test
    void testGetUserProfile_Success() {
        UserProfileResponseDto expectedResponse = new UserProfileResponseDto();
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(UserProfileResponseDto.class)))
                .thenReturn(expectedResponse);

        UserProfileResponseDto result = userServiceInteractor.getUserProfile(1L);

        assertNotNull(result);
    }

  ///  @Test
    void testGetUserProfile_Exception() {
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(UserProfileResponseDto.class)))
                .thenThrow(new RuntimeException("Simulated restTemplate exception"));

        UserProfileNotFoundException exception = assertThrows(UserProfileNotFoundException.class,
                () -> userServiceInteractor.getUserProfile(1L));

        assertEquals("Error fetching user profile 1", exception.getMessage());

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Simulated restTemplate exception", exception.getCause().getMessage());
    }
}
