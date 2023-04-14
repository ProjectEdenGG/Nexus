package gg.projecteden.nexus.framework.commandsv2.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;

import java.math.BigDecimal;

public class ArgumentUtils {

	public static boolean isNumber(Class<?> type) {
		return Integer.class == type || Integer.TYPE == type ||
			Double.class == type || Double.TYPE == type ||
			Float.class == type || Float.TYPE == type ||
			Short.class == type || Short.TYPE == type ||
			Long.class == type || Long.TYPE == type ||
			Byte.class == type || Byte.TYPE == type ||
			BigDecimal.class == type;
	}

	public static Number parseNumber(Class<?> clazz, String input) {
		try {
			final Number number;
			if (BigDecimal.class == clazz) number = new BigDecimal(input);
			else if (Integer.class == clazz || Integer.TYPE == clazz) number = Integer.parseInt(input);
			else if (Double.class == clazz || Double.TYPE == clazz) number = Double.parseDouble(input);
			else if (Float.class == clazz || Float.TYPE == clazz) number = Float.parseFloat(input);
			else if (Short.class == clazz || Short.TYPE == clazz) number = Short.parseShort(input);
			else if (Long.class == clazz || Long.TYPE == clazz) number = Long.parseLong(input);
			else if (Byte.class == clazz || Byte.TYPE == clazz) number = Byte.parseByte(input);
			else throw new InvalidInputException("Unsupported number class " + clazz.getSimpleName());
			return number;
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("&e" + input + " &cis not a valid " + (clazz == BigDecimal.class ? "number" : clazz.getSimpleName().toLowerCase()));
		}
	}
}
