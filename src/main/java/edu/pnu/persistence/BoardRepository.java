package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Receipt;

public interface BoardRepository extends JpaRepository<Receipt, Long> {
}
