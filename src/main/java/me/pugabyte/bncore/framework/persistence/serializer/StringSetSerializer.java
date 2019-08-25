package me.pugabyte.bncore.framework.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
