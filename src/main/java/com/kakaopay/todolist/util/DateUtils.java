package com.kakaopay.todolist.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	public static String getNowAsString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
