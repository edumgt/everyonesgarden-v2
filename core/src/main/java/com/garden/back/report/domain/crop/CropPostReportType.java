package com.garden.back.report.domain.crop;

import lombok.Getter;

@Getter
public enum CropPostReportType {
    SPAMMING("도배글", 1),
    SWEAR_WORD("욕설", 3),
    SENSATIONAL("선정성", 5),
    PERSONAL_INFORMATION_EXPOSURE("개인정보노출", 2),
    OFFENSIVE_EXPRESSION("불쾌한 표현", 3);

    private final String description;
    private final int score;

    CropPostReportType(String description, int score) {
        this.description = description;
        this.score = score;
    }
}
