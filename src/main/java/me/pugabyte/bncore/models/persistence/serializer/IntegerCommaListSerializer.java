package me.pugabyte.bncore.models.persistence.serializer;

import com.dieselpoint.norm.serialize.DbSerializable;
import me.pugabyte.bncore.BNCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegerCommaListSerializer implements DbSerializable {
	@Override
	public String serialize(Object in) {
		BNCore.log("Hi! " + in);
		if (in == null) return null;
		return String.join(",", (List) in);
	}

	@Override
	public Object deserialize(String in, Class<?> targetClass) {
		BNCore.log("Hi! " + in);
		if (in == null) return null;
		List<Integer> ints = new ArrayList<>();
		Arrays.asList(in.split(",")).forEach(string -> ints.add(Integer.parseInt(string)));
		return ints;
	}
}
