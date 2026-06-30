package com.tara.western;
import lombok.Data;

@Data
public class WesternChartDto {
    private String id;
    private String sunSign;
    private String moonSign;
    private String ascendant;
    private Object planetPositions;
    private Object houses;
    private Object aspects;
    private String generatedAt;
}
