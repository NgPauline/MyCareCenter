package com.example.demo.repository;

import com.example.demo.model.Facture;
import com.example.demo.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Integer> {

    List<Facture> findByResident(Resident resident);

    // Pour vérifier les factures impayées d’un résident
    boolean existsByResident_IdPersonneAndStatut(Integer idPersonne, String statut);

    // Pour le dashboard : nombre de factures EN_ATTENTE
    long countByStatut(String statut);

    // Pour le dashboard : dernières factures
    List<Facture> findTop5ByOrderByIdFactureDesc();

    @Query("""
       SELECT f FROM Facture f
       WHERE CAST(f.idFacture AS string) LIKE %:kw%
          OR LOWER(f.statut) LIKE %:kw%
          OR CAST(f.dateEmission AS string) LIKE %:kw%
          OR CAST(f.montant AS string) LIKE %:kw%
          OR LOWER(f.resident.nom) LIKE %:kw%
          OR LOWER(f.resident.prenom) LIKE %:kw%
       """)
List<Facture> search(@Param("kw") String keyword);

   @Query("""
      SELECT f FROM Facture f
      WHERE CAST(f.idFacture AS string) LIKE %:kw%
         OR LOWER(f.statut) LIKE %:kw%
         OR CAST(f.dateEmission AS string) LIKE %:kw%
         OR CAST(f.montant AS string) LIKE %:kw%
         OR LOWER(f.resident.nom) LIKE %:kw%
         OR LOWER(f.resident.prenom) LIKE %:kw%
      """)
   Page<Facture> search(@Param("kw") String keyword, Pageable pageable);

   @Query("SELECT FUNCTION('DATE_FORMAT', f.dateEmission, '%Y-%m'), SUM(f.montant) " +
         "FROM Facture f WHERE f.dateEmission >= :start " +
         "GROUP BY FUNCTION('DATE_FORMAT', f.dateEmission, '%Y-%m')")
   List<Object[]> sumMontantGroupedByMonth(@Param("start") LocalDate start);

   @Query("SELECT FUNCTION('DATE_FORMAT', f.dateEmission, '%Y-%m'), SUM(f.montant) " +
         "FROM Facture f WHERE f.statut = 'PAYEE' AND f.dateEmission >= :start " +
         "GROUP BY FUNCTION('DATE_FORMAT', f.dateEmission, '%Y-%m')")
   List<Object[]> sumMontantPayeeGroupedByMonth(@Param("start") LocalDate start);
}
