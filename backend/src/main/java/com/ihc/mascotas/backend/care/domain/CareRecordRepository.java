package com.ihc.mascotas.backend.care.domain;

import java.util.List;
import java.util.UUID;

public interface CareRecordRepository {

    CareRecord save(CareRecord careRecord);

    List<CareRecord> findByUserId(UUID userId);
}
