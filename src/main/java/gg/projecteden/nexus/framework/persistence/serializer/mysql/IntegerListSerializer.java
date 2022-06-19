package gg.projecteden.nexus.framework.persistence.serializer.mysql;

import com.dieselpoint.norm.serialize.DbSerializable;
import gg.projecteden.api.common.utils.Nullables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerListSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		if (in == null) return null;
		return ((List<Integer>) in).stream().map(String::valueOf).collect(Collectors.joining(","));
	}

	@Override
	public List<Integer> deserialize(String in) {
		List<Integer> ints = new ArrayList<>();
		if (Nullables.isNullOrEmpty(in)) return ints;
		Arrays.asList(in.split(",")).forEach(string -> ints.add(Integer.parseInt(string)));
		return ints;
	}
}
