package com.frauddetection.repository;

import com.frauddetection.model.BlacklistedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedCountryRepository extends JpaRepository<BlacklistedCountry, String> {
    boolean existsByCountryCode(String countryCode);
}