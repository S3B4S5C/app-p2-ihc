package com.ihc.mascotas.backend.care.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CareRecordRepository {

    CareRecord save(CareRecord careRecord);

    List<CareRecord> findByUserId(UUID userId);

    Optional<CareRecord> findById(UUID id);
}
