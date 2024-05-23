package com.demo.mongo.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class LocalDateTimeWriteConverter implements Converter<LocalDateTime, Date> {

	@Override
    public Date convert(LocalDateTime localdDateTime) {
		
		Instant instant = localdDateTime.plusHours(9).atZone(ZoneId.systemDefault()).toInstant();    
		Date date = Date.from(instant);
		return date;
//		return java.sql.Timestamp.valueOf(localdDateTime.plusHours(9));
    }

}