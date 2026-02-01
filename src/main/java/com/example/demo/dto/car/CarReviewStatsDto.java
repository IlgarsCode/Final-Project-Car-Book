package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarReviewStatsDto {
    private long total;

    private long count5;
    private long count4;
    private long count3;
    private long count2;
    private long count1;

    private int percent5;
    private int percent4;
    private int percent3;
    private int percent2;
    private int percent1;
}
