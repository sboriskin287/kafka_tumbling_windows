package ru.egartech.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LocalDateTimeJsonSerializer extends JsonSerializer<List<LocalDateTime>> {
    @Override
    public void serialize(List<LocalDateTime> localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (LocalDateTime ldt : localDateTime) {
            jsonGenerator.writeString(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        jsonGenerator.writeEndArray();
    }
}
