package com.demo.mongo.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DataUtil {

	
	public List<Object> iteratorToList(Iterable t ) {
		List<Object> list = new ArrayList<>();
		
		Iterator it = t.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
}
