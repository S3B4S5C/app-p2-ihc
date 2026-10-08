package com.ihc.mascotas.backend.care.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCareRecordRequest(
        @NotBlank String petName,
        @NotBlank String care,
        @NotBlank String animalType,
        @NotNull LocalDate careDate
) {

}
