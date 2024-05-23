package com.demo.mongo.converter;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class LocalDateTimeReadConverter implements Converter<Date, LocalDateTime> {

	@Override
    public LocalDateTime convert(Date date) {
		
		LocalDateTime localDate = new java.sql.Timestamp(date.getTime()).toLocalDateTime();  // java.util.Date -> java.sql.Date                .toLocalDate();
		return localDate;
		
    }

}
 
