package com.ihc.mascotas.backend.care.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "care_record")
public class CareRecordJpaEntity {
    @Id private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "care")
    private String care;

    @Column(name = "animal_type")
    private String animalType;

    @Column(name = "care_date")
    private LocalDate careDate;

    @Column(name = "created_at")
    private Instant createdAt;

    protected CareRecordJpaEntity() {}

    public CareRecordJpaEntity(UUID id, UUID userId, String petName, String care, String animalType, LocalDate careDate, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.petName = petName;
        this.care = care;
        this.animalType = animalType;
        this.careDate = careDate;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPetName() {
        return petName;
    }

    public String getCare() {
        return care;
    }

    public String getAnimalType() {
        return animalType;
    }

    public LocalDate getCareDate() {
        return careDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
