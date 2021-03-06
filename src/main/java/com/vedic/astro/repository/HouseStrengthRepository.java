package com.vedic.astro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vedic.astro.domain.HouseStrengths;
import com.vedic.astro.enums.PredictionSystem;

/**
 * The Repository which does all basic CRUD operations on the
 * <tt>Token</tt> object. Uses Spring's {@link MongoTemplate} to perform CRUD
 * operations.
 * 
 * @author Sumeer Saxena
 * @param <T>
 */
@Repository("houseStrengthRepository")
public interface HouseStrengthRepository extends CrudRepository<HouseStrengths, String>{

	@Query(value="{'memberId' : ?0, 'predictionSystem' : ?1}")
	public Optional<HouseStrengths> findByMemberIdAndPredictionSystem(String memberId, PredictionSystem predictionSystem);

}