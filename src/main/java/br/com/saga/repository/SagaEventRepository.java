package br.com.saga.repository;

import br.com.saga.domain.entity.SagaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaEventRepository extends JpaRepository<SagaEvent, Long> {
    List<SagaEvent> findBySagaIdOrderByCreatedAtAsc(String sagaId);
}
