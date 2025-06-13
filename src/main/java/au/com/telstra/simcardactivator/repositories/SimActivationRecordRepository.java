package au.com.telstra.simcardactivator.repositories;

// Spring Data JPA import for JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Optional, but good practice for clarity

import au.com.telstra.simcardactivator.models.SimActivationRecord;

/**
 * JPA Repository interface for SimActivationRecord entities.
 * Spring Data JPA automatically provides implementations for basic CRUD operations
 * (Create, Read, Update, Delete) and common query methods
 * by extending JpaRepository.
 *
 * The first type parameter (SimActivationRecord) is the entity type,
 * and the second (Long) is the type of the entity's primary key (ID).
 */
@Repository // Indicates that this interface is a "Repository", a mechanism for encapsulating storage, retrieval, and search behavior.
public interface SimActivationRecordRepository extends JpaRepository<SimActivationRecord, Long> {
    // No methods are needed here for basic CRUD operations.
    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), deleteById(), etc.
    // Custom query methods can be added here if needed (e.g., findByIccid(String iccid)).
}

