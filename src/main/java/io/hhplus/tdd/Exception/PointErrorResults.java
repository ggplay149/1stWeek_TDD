package io.hhplus.tdd.Exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorResults {
    ID_NOT_FOUND(HttpStatus.NOT_FOUND,"ID_NOT_FOUND"),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND,"USER_ID_NOT_FOUND"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST,"INSUFFICIENT_BALANCE"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
