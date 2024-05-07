package edu.usc.csci310.project.repository;

import edu.usc.csci310.project.domain.Activity;
import edu.usc.csci310.project.dto.ActivityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> findByNameIgnoreCase(String name);

    @Query("SELECT new edu.usc.csci310.project.dto.ActivityDto(a.id, a.name) FROM Activity a JOIN a.parks p WHERE p.id = :parkId")
    List<ActivityDto> findAllActivityDto(Long parkId);

    @Query("SELECT a FROM Activity a JOIN a.parks p WHERE p.fullName = :parkName")
    List<Activity> findByParkName(@Param("parkName") String parkName);
}
