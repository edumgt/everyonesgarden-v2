package com.garden.back.garden.domain.dto;

import com.garden.back.garden.domain.vo.GardenStatus;
import com.garden.back.garden.domain.vo.GardenType;

import java.time.LocalDate;

public record GardenUpdateDomainRequest(
        String gardenName,
        String price,
        String size,
        GardenStatus gardenStatus,
        GardenType gardenType,
        String contact,
        String address,
        Double latitude,
        Double longitude,
        String gardenFacilities,
        String gardenDescription,
        String recruitStartDate,
        String recruitEndDate,
        Long writerId
) {
}
