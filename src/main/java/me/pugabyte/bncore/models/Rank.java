package me.pugabyte.bncore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Rank {
	GUEST("&7", false, false),
	MEMBER("&f", false, false),
	TRUSTED("&e", false, false),
	ELITE("&6", false, false),
	VETERAN("&6&l", true, false),
	BUILDER("&5", true, true),
	ARCHITECT("&5&l", true, true),
	MODERATOR("&b&o", true, true),
	OPERATOR("&3&o", true, true),
	ADMIN("&9&o", true, true),
	OWNER("&4&o", true, true);

	@Getter
	private String format;
	@Getter
	@Accessors(fluent = true)
	private boolean hasPrefix;
	@Getter
	@Accessors(fluent = true)
	private boolean isStaff;

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

	public List<Nerd> getNerds() {
		Set<PermissionUser> users = PermissionsEx.getPermissionManager().getGroup(Utils.camelCase(name())).getUsers();
		Set<Nerd> nerds = new HashSet<>();
		users.forEach(user -> nerds.add(new Nerd(user.getName())));
		return new ArrayList<>(nerds);
	}

	public static List<Rank> getStaff() {
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).collect(Collectors.toList());
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
