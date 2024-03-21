package io.hhplus.tdd.Exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointException extends RuntimeException{
    private final PointErrorResults pointErrorResults;
}
