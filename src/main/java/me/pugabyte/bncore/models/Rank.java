package me.pugabyte.bncore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
			return "&8&l[" + toString() + "&8&l]";
		}

		return "";
	}

	@Override
	public String toString() {
		return format + Utils.camelCase(name());
	}

	public List<Nerd> getNerds() {
		Set<PermissionUser> users = PermissionsEx.getPermissionManager().getGroup(Utils.camelCase(name())).getUsers();
		Set<Nerd> nerds = new HashSet<>();
		users.forEach(user -> nerds.add(new NerdService().get(user.getIdentifier())));
		return new ArrayList<>(nerds);
	}

	public List<Nerd> getOnlineNerds() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank() == this)
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static List<Rank> getStaff() {
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineStaff() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isStaff())
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static Rank getHighestRank(Player player) {
		return getHighestRank(Bukkit.getOfflinePlayer(player.getUniqueId()));
	}

	public static Rank getHighestRank(OfflinePlayer player) {
		PermissionUser user = PermissionsEx.getUser(player.getUniqueId().toString());

		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		for (Rank rank : ranks)
			if (user.inGroup(rank.name()))
				return rank;

		return GUEST;
	}
}
