package com.garden.back.docs.crop;

import com.garden.back.crop.CropController;
import com.garden.back.crop.service.CropService;
import com.garden.back.crop.service.response.MonthlyRecommendedCropsResponse;
import com.garden.back.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CropRestDocs extends RestDocsSupport {

    CropService cropService = mock(CropService.class);

    @Override
    protected Object initController() {
        return new CropController(cropService);
    }

    @DisplayName("1월~12월 까지의 작물을 추천하는 API DOCS")
    @Test
    void getMonthlyRecommendedCrops() throws Exception {
        MonthlyRecommendedCropsResponse response = CropFixture.createMonthlyRecommendedCropsResponse();
        given(cropService.getMonthlyRecommendedCrops()).willReturn(response);

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
}
