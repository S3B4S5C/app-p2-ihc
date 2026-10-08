package com.ihc.mascotas.backend.care.application;

import com.ihc.mascotas.backend.auth.domain.User;
import com.ihc.mascotas.backend.auth.domain.UserRepository;
import com.ihc.mascotas.backend.care.domain.CareRecord;
import com.ihc.mascotas.backend.care.domain.CareRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CareRecordService {
    private final CareRecordRepository careRecordRepository;
    private final UserRepository userRepository;

    public CareRecordService(CareRecordRepository careRecordRepository, UserRepository userRepository) {
        this.careRecordRepository = careRecordRepository;
        this.userRepository = userRepository;
    }

    public CareRecord create(
            String email,
            String petName,
            String care,
            String animalType,
            LocalDate careDate
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CareRecord record = new CareRecord(
                UUID.randomUUID(),
                user.id(),
                petName,
                care,
                animalType,
                careDate,
                Instant.now()
        );

        return careRecordRepository.save(record);
    }

    public List<CareRecord> findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return careRecordRepository.findByUserId(user.id());
    }
}
