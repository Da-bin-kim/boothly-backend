package com.boothly.backend.common.exception.user;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND
                , "사용자를 찾을 수 없습니다."
                        + " userId=" + userId);
    }
}
