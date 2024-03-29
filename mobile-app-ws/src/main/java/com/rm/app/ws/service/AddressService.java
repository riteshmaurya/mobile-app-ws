package com.rm.app.ws.service;

import java.util.List;

import com.rm.app.ws.shared.dto.AddressDto;

public interface AddressService {
	
	List<AddressDto> getAddresses(String userId);

	AddressDto getAddress(String addressId);
	
}
