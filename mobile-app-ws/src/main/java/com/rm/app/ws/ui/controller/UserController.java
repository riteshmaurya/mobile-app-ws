package com.rm.app.ws.ui.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rm.app.ws.exceptions.UserServiceException;
import com.rm.app.ws.service.AddressService;
import com.rm.app.ws.service.UserService;
import com.rm.app.ws.shared.dto.AddressDto;
import com.rm.app.ws.shared.dto.UserDto;
import com.rm.app.ws.ui.model.request.UserDetailsRequestModel;
import com.rm.app.ws.ui.model.response.AddressRest;
import com.rm.app.ws.ui.model.response.OperationStatusModel;
import com.rm.app.ws.ui.model.response.RequestOperationName;
import com.rm.app.ws.ui.model.response.RequestOperationStatus;
import com.rm.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@Autowired
	ModelMapper modelMapper;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {

		UserDto userDto = userService.getUserByUserId(id);
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);
		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {

		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("The object is null");

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);

		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("The object is null");

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto createdUser = userService.updateUser(id, userDto);

		BeanUtils.copyProperties(createdUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "limit", defaultValue = "4") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<AddressRest> getUserAddresses(@PathVariable String id) {

		List<AddressDto> addressesDto = addressService.getAddresses(id);
		List<AddressRest> returnValue = null;
		if (addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			returnValue = modelMapper.map(addressesDto, listType);
		}

		return returnValue;
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDto addressesDto = addressService.getAddress(addressId);

		AddressRest returnValue = modelMapper.map(addressesDto, AddressRest.class);

		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		returnValue.add(addressLink);
		returnValue.add(userLink);
		returnValue.add(addressesLink);
		return returnValue;
	}

	
	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam("token") String token) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		boolean isVerified = userService.verifyEmailToken(token);
		if(isVerified){
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}else{
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		return returnValue;
	}
}
