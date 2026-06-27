package br.com.saga.domain.entity;

import br.com.saga.domain.enums.SagaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saga_events")
public class SagaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false, length = 64)
    private String sagaId;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SagaStatus status;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
