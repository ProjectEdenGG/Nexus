package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;

import java.util.Arrays;
import java.util.List;

public class StringCommaListSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		if (in == null) return null;
		return String.join(",", (List) in);
	}

	@Override
	public Object deserialize(String in, Class<?> targetClass) {
		if (in == null) return null;
		return Arrays.asList(in.split(","));
	}
}
