package com.rm.app.ws.repository;

import org.springframework.data.repository.CrudRepository;

import com.rm.app.ws.io.entity.AddressEntity;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

	AddressEntity findByAddressId(String addressId);
}
