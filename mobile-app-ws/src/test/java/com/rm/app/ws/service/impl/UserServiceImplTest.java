package com.rm.app.ws.service.impl;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import com.rm.app.ws.io.entity.UserEntity;
import com.rm.app.ws.repository.UserRepository;
import com.rm.app.ws.shared.dto.UserDto;

public class UserServiceImplTest {
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	UserServiceImpl userService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetUser() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("DSFasg");
		userEntity.setUserId("Asdsfasffas");
		userEntity.setEncryptedPassword("sdaafxgrww");
			when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
			
			UserDto userDto = userService.getUser("test@sdfsaf");
			
			assertNotNull(userDto);
			
			assertEquals("DSFasg", userDto.getFirstName());
			
	}
}
