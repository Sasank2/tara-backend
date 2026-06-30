package com.tara.planets;
import lombok.Data;

@Data
public class PlanetsDto {
    private String date;
    private String sunPosition;
    private String moonPosition;
    private String mercuryPosition;
    private String venusPosition;
    private String marsPosition;
    private String jupiterPosition;
    private String saturnPosition;
    private Object planetDetails;
}
