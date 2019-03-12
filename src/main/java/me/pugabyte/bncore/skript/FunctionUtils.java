package me.pugabyte.bncore.skript;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;

public class FunctionUtils {
	public static Object[] executeFunction(String name, Object[][] params) throws IllegalArgumentException {
		Function function = Functions.getFunction(name);
		if (function == null) {
			throw new IllegalArgumentException();
		}

		Object[] val = function.execute(params);
		function.resetReturnValue();
		return val;
	}
}