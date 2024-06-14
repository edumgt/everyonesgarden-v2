package com.garden.back.garden.repository.garden;

import com.garden.back.garden.domain.Garden;
import com.garden.back.garden.repository.garden.dto.GardenByName;
import com.garden.back.garden.repository.garden.dto.GardenGetAll;
import com.garden.back.garden.repository.garden.dto.GardensByComplexes;
import com.garden.back.garden.repository.garden.dto.GardensByComplexesWithScroll;
import com.garden.back.garden.repository.garden.dto.request.GardenByComplexesRepositoryRequest;
import com.garden.back.garden.repository.garden.dto.request.GardenByComplexesWithScrollRepositoryRequest;
import com.garden.back.garden.repository.garden.dto.response.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface GardenRepository {
    Slice<GardenByName> findGardensByName(String gardenName, Pageable pageable);

    Slice<GardenGetAll> getAllGardens(Pageable pageable);

    GardensByComplexesWithScroll getGardensByComplexesWithScroll(GardenByComplexesWithScrollRepositoryRequest request);

    Garden save(Garden garden);

    List<GardenDetailRepositoryResponse> getGardenDetail(
        Long memberId,
        Long gardenId
    );

    Garden getById(Long gardenId);

    void deleteById(Long gardenId);

    GardenMineRepositoryResponses findByWriterId(Long writerId,Long nextGardenId, Pageable pageable);

    List<GardenLikeByMemberRepositoryResponse> getLikeGardenByMember(Long memberId);

    List<GardenChatRoomInfoRepositoryResponse> getChatRoomInfo(Long gardenId);

    List<RecentCreateGardenRepositoryResponse> getRecentCreatedGardens(Long memberId);

    Optional<GardenLocationRepositoryResponse> findGardenLocation(Long gardenId);

    GardenLocationRepositoryResponse getGardenLocation(Long gardenId);

    GardensByComplexes getGardensByComplexes(GardenByComplexesRepositoryRequest request);

}
