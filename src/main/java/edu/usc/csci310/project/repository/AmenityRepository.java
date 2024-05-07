package edu.usc.csci310.project.repository;

import edu.usc.csci310.project.domain.Amenity;
import edu.usc.csci310.project.dto.AmenityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    @Query("SELECT new edu.usc.csci310.project.dto.AmenityDto(a.id, a.name) FROM Amenity a JOIN a.parks p WHERE p.id = :parkId")
    List<AmenityDto> findAllAmenities(Long parkId);
}
