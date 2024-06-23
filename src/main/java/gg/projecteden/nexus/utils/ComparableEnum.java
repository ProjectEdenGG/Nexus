package gg.projecteden.nexus.utils;

public interface ComparableEnum {
	int ordinal();

	default boolean gt(Enum<?> other) {
		return ordinal() > other.ordinal();
	}

	default boolean gte(Enum<?> other) {
		return ordinal() >= other.ordinal();
	}

	default boolean lt(Enum<?> other) {
		return ordinal() < other.ordinal();
	}

	default boolean lte(Enum<?> other) {
		return ordinal() <= other.ordinal();
	}

}
