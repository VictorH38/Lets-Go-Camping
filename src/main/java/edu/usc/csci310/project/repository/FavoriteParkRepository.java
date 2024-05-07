package edu.usc.csci310.project.repository;

import edu.usc.csci310.project.domain.FavoritePark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteParkRepository extends JpaRepository<FavoritePark, Long> {
    Optional<FavoritePark> findByUserIdAndParkId(Long userId, Long parkId);
    List<FavoritePark> findAllByUserIdOrderByRankAsc(Long userId);
}
