package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommaSeparatedSetSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		// if (in == null) return null;
		return String.join(",", (Set) in);
	}

	@Override
	public Object deserialize(String in, Class<?> targetClass) {
		// if (in == null) return null;
		return new HashSet<>(Arrays.asList(in.split(",")));
	}
}
