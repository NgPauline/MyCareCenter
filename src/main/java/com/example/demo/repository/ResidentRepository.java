package com.example.demo.repository;

import com.example.demo.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ResidentRepository extends JpaRepository<Resident, Integer> {

    boolean existsByIdResident(Integer idResident);

    Resident findByIdResident(Integer idResident);

    List<Resident> findByStatut(String statut);

    List<Resident> findByDateAdmissionBetween(LocalDate debut, LocalDate fin);

    // Résidents sans chambre
    List<Resident> findByChambreIsNull();

    // Pour le dashboard : les 5 derniers résidents
    List<Resident> findTop5ByOrderByIdPersonneDesc();

    @Query("""
       SELECT r FROM Resident r
       WHERE LOWER(r.nom) LIKE LOWER(CONCAT('%', :kw, '%'))
          OR LOWER(r.prenom) LIKE LOWER(CONCAT('%', :kw, '%'))
          OR LOWER(CONCAT(r.nom, ' ', r.prenom)) LIKE LOWER(CONCAT('%', :kw, '%'))
       """)
List<Resident> search(@Param("kw") String keyword);

@Query("""
   SELECT r FROM Resident r
   WHERE LOWER(r.nom) LIKE LOWER(CONCAT('%', :kw, '%'))
      OR LOWER(r.prenom) LIKE LOWER(CONCAT('%', :kw, '%'))
      OR LOWER(CONCAT(r.nom, ' ', r.prenom)) LIKE LOWER(CONCAT('%', :kw, '%'))
   """)
Page<Resident> search(@Param("kw") String keyword, Pageable pageable);

   @Query("SELECT FUNCTION('DATE_FORMAT', r.dateAdmission, '%Y-%m'), COUNT(r) " +
         "FROM Resident r WHERE r.dateAdmission >= :start " +
         "GROUP BY FUNCTION('DATE_FORMAT', r.dateAdmission, '%Y-%m')")
   List<Object[]> countGroupedByMonth(@Param("start") LocalDate start);


   @Query("SELECT COALESCE(MAX(r.idResident), 001) FROM Resident r")
   Integer findMaxIdResident();


}
