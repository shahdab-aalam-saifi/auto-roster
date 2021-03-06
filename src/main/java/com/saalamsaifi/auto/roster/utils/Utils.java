package com.saalamsaifi.auto.roster.utils;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

	public static String getObjectId() {
		return ObjectId.get().toHexString();
	}

	public static <T> T stringToObject(String json, ObjectMapper mapper, Class<T> clazz) {
		if (mapper == null) {
			throw new IllegalStateException("mapper == null");
		}

		try {
			return mapper.readValue(json, clazz);
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}
}
