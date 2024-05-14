package com.demo.mongo.converter;

import java.time.OffsetDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {

	@Override
    public Date convert(OffsetDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());//.plusHours(9)
    }

}