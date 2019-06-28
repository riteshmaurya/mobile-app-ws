package com.rm.app.ws.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rm.app.ws.io.entity.AddressEntity;
import com.rm.app.ws.io.entity.UserEntity;
import com.rm.app.ws.repository.AddressRepository;
import com.rm.app.ws.repository.UserRepository;
import com.rm.app.ws.service.AddressService;
import com.rm.app.ws.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValue = new ArrayList<>();
		UserEntity user = userRepository.findByUserId(userId);
		Type listType = new TypeToken<List<AddressDto>>() {}.getType();
		returnValue= modelMapper.map(user.getAddresses(), listType);
		return returnValue;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		System.out.println(addressEntity.getCountry());
		AddressDto returnValue = modelMapper.map(addressEntity, AddressDto.class);
		return returnValue;
	}

}
