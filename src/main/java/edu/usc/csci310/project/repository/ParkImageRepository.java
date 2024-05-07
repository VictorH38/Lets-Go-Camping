package edu.usc.csci310.project.repository;

import edu.usc.csci310.project.domain.ParkImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ParkImageRepository extends JpaRepository<ParkImage, Long> {
    List<ParkImage> findByParkId(Long parkId);
}
