package com.rm.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rm.app.ws.exceptions.UserServiceException;
import com.rm.app.ws.io.entity.UserEntity;
import com.rm.app.ws.repository.UserRepository;
import com.rm.app.ws.service.UserService;
import com.rm.app.ws.shared.Utils;
import com.rm.app.ws.shared.dto.AddressDto;
import com.rm.app.ws.shared.dto.UserDto;
import com.rm.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private Utils utils;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("User already  exists!");

		user.getAddresses().stream().map(temp -> {

			AddressDto addressDto = temp;
			addressDto.setUserDetails(user);
			addressDto.setAddressId(utils.generateAddressId(30));
			return addressDto;
		}).collect(Collectors.toList());

		// for(int i=0;i<user.getAddresses().size();i++){
		// AddressDto add = user.getAddresses().get(i);
		// add.setUserDetails(user);
		// add.setAddressId(utils.generateAddressId(30));
		// user.getAddresses().set(i, add);
		// }

		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		System.out.println(userEntity.getAddresses().get(0).getAddressId());
		String publicUserId = utils.generateUserId(30);

		userEntity.setUserId(publicUserId);

		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		UserEntity storedUserDetails = userRepository.save(userEntity);

		UserDto returnedValue = modelMapper.map(storedUserDetails, UserDto.class);
		System.out.println(returnedValue.getEmail());
		return returnedValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = this.userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
		// return new User(userEntity.getEmail(),
		// userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("user with id " + userId + " not found");
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUserDetails = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		page = (page > 0) ? page - 1 : 0;
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;

		// Find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

		if (userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if (!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}

		return returnValue;
	}

}
