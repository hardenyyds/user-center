package com.wwh.usercenter.exception;

import com.wwh.usercenter.common.BaseResponse;
import com.wwh.usercenter.common.ErrorCode;
import com.wwh.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleBusinessException(BusinessException e){
        return ResultUtils.error(e.getCode(),e.getMessage(),"");
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleRunTimeException(BusinessException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
