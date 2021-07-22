package gg.projecteden.nexus.framework.persistence.serializer.mysql;

import com.dieselpoint.norm.serialize.DbSerializable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringSetSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		return String.join(",", ((Set<String>) in));
	}

	@Override
	public Set<String> deserialize(String in) {
		return new HashSet<>(Arrays.asList(in.split(",")));
	}
}
