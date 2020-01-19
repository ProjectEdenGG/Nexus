package me.pugabyte.bncore.models;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Rank {
	GUEST("&7", false),
	MEMBER("&f", false),
	TRUSTED("&e", false),
	ELITE("&6", false),
	VETERAN("&6&l", true),
	BUILDER("&5", true),
	ARCHITECT("&5&l", true),
	MODERATOR("&b&o", true),
	OPERATOR("&3&o", true),
	ADMIN("&9&o", true),
	OWNER("&4&o", true);

	@Getter
	private String format;
	@Getter
	@Accessors(fluent = true)
	private boolean hasPrefix;

	Rank(String format, boolean hasPrefix) {
		this.format = format;
		this.hasPrefix = hasPrefix;
	}

	public String getPrefix() {
		if (hasPrefix) {
			return "&8&l[" + format + toString() + "&8&l]";
		}

		return "";
	}

	@Override
	public String toString() {
		return Utils.camelCase(format + name());
	}

	public static Rank getHighestRank(Player player) {
		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		for (Rank rank : ranks)
			if (player.hasPermission("rank." + rank.name().toLowerCase()))
				return rank;

		return GUEST;
	}
}
