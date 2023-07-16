package ru.egartech.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TumblingWindow {
    private Integer sum = 0;
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private List<LocalDateTime> paymentInstants = new ArrayList<>();
}
