package me.pugabyte.nexus.utils;

import eden.utils.Env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class EnumUtils {
	public static <T> T valueOf(Class<? extends T> clazz, String value) {
		T[] values = clazz.getEnumConstants();
		for (T enumValue : values)
			if (((Enum<?>) enumValue).name().equalsIgnoreCase(value))
				return enumValue;
		throw new IllegalArgumentException();
	}

	public static <T> T next(Class<? extends T> clazz, int ordinal) {
		T[] values = clazz.getEnumConstants();
		return values[Math.min(values.length - 1, ordinal + 1 % values.length)];
	}

	public static <T> T previous(Class<? extends T> clazz, int ordinal) {
		T[] values = clazz.getEnumConstants();
		return values[Math.max(0, ordinal - 1 % values.length)];
	}

	public static <T> T nextWithLoop(Class<? extends T> clazz, int ordinal) {
		T[] values = clazz.getEnumConstants();
		int next = ordinal + 1 % values.length;
		return next >= values.length ? values[0] : values[next];
	}

	public static <T> T previousWithLoop(Class<? extends T> clazz, int ordinal) {
		T[] values = clazz.getEnumConstants();
		int previous = ordinal - 1 % values.length;
		return previous < 0 ? values[values.length - 1] : values[previous];
	}

	public static <T> T random(Class<? extends T> clazz) {
		return RandomUtils.randomElement(clazz.getEnumConstants());
	}

	public static <T> List<String> valueNameList(Class<? extends T> clazz) {
		return Arrays.stream(Env.values()).map(Env::name).collect(Collectors.toList());
	}

	public static <T> List<T> valuesExcept(Class<? extends T> clazz, Enum<?>... exclude) {
		List<Enum<?>> excluded = Arrays.asList(exclude);
		List<Enum<?>> values = new ArrayList<>();
		for (T enumValue : clazz.getEnumConstants())
			if (!excluded.contains(enumValue))
				values.add((Enum<?>) enumValue);

		return (List<T>) values;
	}

	public static String prettyName(String name) {
		if (!name.contains("_"))
			return camelCase(name);

		List<String> words = new ArrayList<>(Arrays.asList(name.split("_")));

		String first = words.get(0);
		String last = words.get(words.size() - 1);
		words.remove(0);
		words.remove(words.size() - 1);

		StringBuilder result = new StringBuilder(camelCase(first));
		for (String word : words) {
			String character = interpolate(word);
			if (character != null)
				result.append(character);
			else if (word.toLowerCase().matches("and|for|the|a|or|of|from|in|as"))
				result.append(" ").append(word.toLowerCase());
			else
				result.append(" ").append(camelCase(word));
		}

		String character = interpolate(last);
		if (character != null)
			result.append(character);
		else
			result.append(" ").append(last.charAt(0)).append(last.substring(1).toLowerCase());
		return result.toString().trim();
	}

	private static String interpolate(String word) {
		String character = switch (word.toLowerCase()) {
			case "period" -> ".";
			case "excl" -> "!";
			case "comma" -> ",";
			default -> null;
		};
		return character;
	}

	public interface IteratableEnum {
		int ordinal();

		String name();

		default <T extends Enum<?>> T next() {
			return (T) EnumUtils.next(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T previous() {
			return (T) EnumUtils.previous(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T nextWithLoop() {
			return (T) EnumUtils.nextWithLoop(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T previousWithLoop() {
			return (T) EnumUtils.previousWithLoop(this.getClass(), ordinal());
		}
	}

}
