package com.garden.back.docs.crop;

import com.garden.back.crop.CropController;
import com.garden.back.crop.CropQueryService;
import com.garden.back.crop.FindAllCropsPostResponse;
import com.garden.back.crop.FindCropsPostDetailsResponse;
import com.garden.back.crop.domain.CropCategory;
import com.garden.back.crop.domain.TradeType;
import com.garden.back.crop.request.CropsPostCreateRequest;
import com.garden.back.crop.request.CropsPostsUpdateRequest;
import com.garden.back.crop.request.FindAllCropsPostRequest;
import com.garden.back.crop.service.CropCommandService;
import com.garden.back.crop.service.response.MonthlyRecommendedCropsResponse;
import com.garden.back.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CropRestDocs extends RestDocsSupport {

    CropCommandService cropCommandService = mock(CropCommandService.class);
    CropQueryService cropQueryService = mock(CropQueryService.class);

    @Override
    protected Object initController() {
        return new CropController(cropCommandService, cropQueryService);
    }

    @DisplayName("1월~12월 까지의 작물을 추천하는 API DOCS")
    @Test
    void getMonthlyRecommendedCrops() throws Exception {
        MonthlyRecommendedCropsResponse response = CropFixture.createMonthlyRecommendedCropsResponse();
        given(cropCommandService.getMonthlyRecommendedCrops()).willReturn(response);

        mockMvc.perform(get("/v1/crops")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-all-months-recommended-crops",
                responseFields(
                    fieldWithPath("cropsResponses").type(JsonFieldType.ARRAY).description("작물 추천 응답 리스트"),
                    fieldWithPath("cropsResponses[].month").type(JsonFieldType.NUMBER).description("월"),
                    fieldWithPath("cropsResponses[].cropInfos").type(JsonFieldType.ARRAY).description("해당 월의 작물 추천 리스트"),
                    fieldWithPath("cropsResponses[].cropInfos[].name").type(JsonFieldType.STRING).description("작물 이름"),
                    fieldWithPath("cropsResponses[].cropInfos[].description").type(JsonFieldType.STRING).description("작물 설명"),
                    fieldWithPath("cropsResponses[].cropInfos[].link").type(JsonFieldType.STRING).description("작물에 대한 상세 링크")
                )
            ));
    }

    @DisplayName("작물 게시글 전체 조회 api docs")
    @Test
    void findAllCropsPosts() throws Exception {
        FindAllCropsPostResponse response = new FindAllCropsPostResponse(List.of(new FindAllCropsPostResponse.CropsInfo(1L, "제목", 100000, LocalDate.now(), TradeType.DIRECT_TRADE, true, true, 2)));
        FindAllCropsPostRequest request = new FindAllCropsPostRequest(0, 10, "RECENT_DATE");
        given(cropQueryService.findAll(any())).willReturn(response);

        mockMvc.perform(get("/v1/crops/posts")
                .param("offset", String.valueOf(request.offset()))
                .param("limit", String.valueOf(request.limit()))
                .param("orderBy", request.orderBy())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-all-crops-posts",
                queryParameters(
                    parameterWithName("offset").description("조회를 시작할 데이터의 위치"),
                    parameterWithName("limit").description("해당 페이지에서 조회할 데이터의 개수"),
                    parameterWithName("orderBy").description("정렬 조건(RECENT_DATE, LIKE_COUNT, OLDER_DATE 중 한개를 입력해주세요)")
                ),
                responseFields(
                    fieldWithPath("cropsInfos").type(ARRAY).description("작물 게시글 정보 목록"),
                    fieldWithPath("cropsInfos[].cropsPostId").type(NUMBER).description("작물 게시글 ID"),
                    fieldWithPath("cropsInfos[].title").type(STRING).description("작물 게시글 제목"),
                    fieldWithPath("cropsInfos[].price").type(NUMBER).description("작물 가격"),
                    fieldWithPath("cropsInfos[].createdDate").type(STRING).description("작물 게시글 생성 일"),
                    fieldWithPath("cropsInfos[].tradeType").type(STRING).description("작물의 거래 형식(직거래, 택배 거래)"),
                    fieldWithPath("cropsInfos[].priceProposal").type(BOOLEAN).description("가격제안 가능여부"),
                    fieldWithPath("cropsInfos[].reservationStatus").type(BOOLEAN).description("예약 상태"),
                    fieldWithPath("cropsInfos[].bookmarkCount").type(NUMBER).description("이 게시글을 북마크힌 이용자의 수")
                )
            ));
    }

    @DisplayName("작물 게시글 상세 조회 api docs")
    @Test
    void findCropsPostsDetails() throws Exception {
        FindCropsPostDetailsResponse response = new FindCropsPostDetailsResponse("내용", "글쓴이", "78.2", "서울시 성동구 금호동", CropCategory.FRUIT, 2, List.of("이미지 url"));
        given(cropQueryService.findCropsPostDetails(any())).willReturn(response);

        mockMvc.perform(get("/v1/crops/posts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-details-crops-post",
                pathParameters(
                    parameterWithName("id").description("작물 게시글 id")
                ),
                responseFields(
                    fieldWithPath("content").type(STRING).description("내용"),
                    fieldWithPath("author").type(STRING).description("작성자"),
                    fieldWithPath("mannerPoint").type(STRING).description("매너 점수"),
                    fieldWithPath("authorAddress").type(STRING).description("작성자 주소"),
                    fieldWithPath("cropCategory").type(STRING).description("작물 카테고리"),
                    fieldWithPath("bookmarkCount").type(NUMBER).description("북마크 수"),
                    fieldWithPath("images").type(ARRAY).description("이미지 URL 리스트")
                )
            ));
    }

    @DisplayName("작물 게시글 생성 api docs")
    @Test
    void createCropsPost() throws Exception {

        MockMultipartFile firstImage = new MockMultipartFile(
            "images",
            "image1.png",
            "image/png",
            "image-files".getBytes()
        );

        CropsPostCreateRequest request = sut.giveMeBuilder(CropsPostCreateRequest.class)
            .set("title", "제목")
            .set("content", "내용")
            .set("cropsCategory", "GRAIN")
            .set("price", 10000)
            .set("priceProposal", Boolean.TRUE)
            .set("tradeType", "DIRECT_TRADE")
            .sample();

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "texts",
            "content",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                multipart("/v1/crops/posts")
                    .file(firstImage)
                    .file(mockMultipartFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("create-crops-posts",
                requestPartFields("texts",
                    fieldWithPath("title").type(STRING).description("작물 게시글 제목"),
                    fieldWithPath("content").type(STRING).description("작물 게시글 내용"),
                    fieldWithPath("cropsCategory").type(STRING).description("작물 카테고리 (GRAIN, VEGETABLE, FRUIT, BEAN 중에서 한개만 입력이 가능합니다.)"),
                    fieldWithPath("price").type(NUMBER).description("가격"),
                    fieldWithPath("priceProposal").type(BOOLEAN).description("가격 제안 여부"),
                    fieldWithPath("tradeType").type(STRING).description("거래 유형 (DIRECT_TRADE, DELIVERY_TRADE 중에서 한개만 입력이 가능합니다.)")
                ),
                requestParts(
                    partWithName("texts").description("게시글 내용 texts에는 json 형식으로 위 part 필드들에 대해 요청해주시면 됩니다."),
                    partWithName("images").description("게시글에 넣을 이미지(10개까지 허옹)").optional()
                ),
                responseHeaders(
                    headerWithName("Location").description("생성된 게시글의 id")
                )
            ));
    }

    @DisplayName("게시글 수정 api docs")
    @Test
    void updateCropsPost() throws Exception {
        MockMultipartFile firstImage = new MockMultipartFile(
            "images",
            "image1.png",
            "image/png",
            "image-files".getBytes()
        );

        CropsPostsUpdateRequest request = sut.giveMeBuilder(CropsPostsUpdateRequest.class)
            .set("title", "제목")
            .set("content", "내용")
            .set("cropsCategory", "GRAIN")
            .set("price", 10000)
            .set("priceProposal", Boolean.TRUE)
            .set("tradeType", "DIRECT_TRADE")
            .set("reservationStatus", false)
            .size("deleteImages", 1)
            .set("deleteImages[0]", "해당 이미지의 url")
            .sample();

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "texts",
            "content",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                multipart("/v1/crops/posts/{postId}", 1L)
                    .file(firstImage)
                    .file(mockMultipartFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    })
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("update-crops-post",
                pathParameters(
                    parameterWithName("postId").description("작물 게시글 id")
                ),
                requestPartFields("texts",
                    fieldWithPath("title").type(STRING).description("작물 게시글 제목"),
                    fieldWithPath("content").type(STRING).description("작물 게시글 내용"),
                    fieldWithPath("cropsCategory").type(STRING).description("작물 카테고리 (GRAIN, VEGETABLE, FRUIT, BEAN 중에서 한개만 입력이 가능합니다.)"),
                    fieldWithPath("price").type(NUMBER).description("가격"),
                    fieldWithPath("priceProposal").type(BOOLEAN).description("가격 제안 여부"),
                    fieldWithPath("tradeType").type(STRING).description("거래 유형 (DIRECT_TRADE, DELIVERY_TRADE 중에서 한개만 입력이 가능합니다.)"),
                    fieldWithPath("reservationStatus").type(BOOLEAN).description("예약 상태"),
                    fieldWithPath("deleteImages").type(ARRAY).description("삭제할 이미지의 url 목록")
                ),
                requestParts(
                    partWithName("texts").description("게시글 내용, 제목, 수정할 때 삭제되는 이미지 url들 texts에는 json 형식으로 위 part 필드들에 대해 요청해주시면 됩니다."),
                    partWithName("images").description("게시글에 넣을 이미지(10개까지 허옹)").optional()
                )
            ));
    }

    @DisplayName("작물 게시글 북마크 api docs")
    @Test
    void createCropsPostBookmarks() throws Exception {

        mockMvc.perform(post("/v1/crops/posts/{id}/bookmarks", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("create-crops-post-bookmark",
                pathParameters(
                    parameterWithName("id").description("작물 게시글 id")
                ),
                responseHeaders(
                    headerWithName("Location").description("생성된 북마크의 id")
                )
            ));
    }

    @DisplayName("작물 게시글의 북마크를 취소할 수 있다.")
    @Test
    void deleteCropsPostBookmark() throws Exception {

        mockMvc.perform(delete("/v1/crops/posts/{id}/bookmarks", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(document("delete-post-crops-bookmark",
                pathParameters(
                    parameterWithName("id").description("북마크 취소할 작물 게시글 id")
                )
            ));
    }


    @DisplayName("작물 게시글을 삭제할 수 있다.")
    @Test
    void deleteCropsPost() throws Exception {

        mockMvc.perform(delete("/v1/crops/posts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(document("delete-post-crops",
                pathParameters(
                    parameterWithName("id").description("삭제할 작물 게시글 id")
                )
            ));
    }
}
