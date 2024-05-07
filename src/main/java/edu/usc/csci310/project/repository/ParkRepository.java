package edu.usc.csci310.project.repository;

import edu.usc.csci310.project.domain.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkRepository extends JpaRepository<Park, Long> {

    List<Park> findByStates(String state);

    List<Park> findByDesignation(String designation);

    @Query("SELECT p FROM Park p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Park> findByNameContaining(@Param("name") String name);
}