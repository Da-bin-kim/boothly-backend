package com.boothly.backend.common.validation;

public final class PageRequestValidator {
    private PageRequestValidator() {
    }

    public static void validate(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("size는 1 이상 50 이하이어야 합니다.");
        }
    }
}
