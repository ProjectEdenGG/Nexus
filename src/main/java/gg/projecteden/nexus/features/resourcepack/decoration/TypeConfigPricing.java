package gg.projecteden.nexus.features.resourcepack.decoration;

public final class TypeConfigPricing {

	private static final int DEFAULT_VALUE = -1;

	public static int tokens(TypeConfig config) {
		int tokens = config.tokens();
		if (tokens != DEFAULT_VALUE)
			return tokens;

		int money = config.money();
		if (money == DEFAULT_VALUE)
			return DEFAULT_VALUE;

		return (money + 5) / 10;
	}

	public static int money(TypeConfig config) {
		return config.money();
	}
}
