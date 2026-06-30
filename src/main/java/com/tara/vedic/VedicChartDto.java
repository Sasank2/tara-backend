package com.tara.vedic;
import lombok.Data;

@Data
public class VedicChartDto {
    private String id;
    private String lagna;
    private String rashi;
    private String nakshatra;
    private Object dasha;
    private Object planetPositions;
    private Object chartJson;
    private String generatedAt;
}
