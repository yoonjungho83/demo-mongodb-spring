package com.demo.mongo.converter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

	@Override
    public OffsetDateTime convert(Date date) {
        return date.toInstant()
        		  .atOffset(ZoneOffset.UTC);
    }

}
 
