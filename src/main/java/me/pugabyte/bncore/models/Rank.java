package me.pugabyte.bncore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.awt.*;
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
	TRUSTED("&e", false, false, new Color(241, 196, 15)),
	ELITE("&6", false, false, new Color(230, 126, 34)),
	VETERAN("&6&l", true, false, new Color(230, 126, 34)),
	BUILDER("&5", true, true, new Color(132, 61, 164)),
	ARCHITECT("&5&l", true, true, new Color(132, 61, 164)),
	MINIGAME_MODERATOR("&b&o", true, true, new Color(25, 211, 211)),
	MODERATOR("&b&o", true, true, new Color(25, 211, 211)),
	OPERATOR("&3&o", true, true, new Color(0, 170, 170)),
	ADMIN("&9&o", true, true, new Color(32, 102, 148)),
	OWNER("&4&o", true, true, new Color(153, 45, 34));

	@Getter
	private String format;
	@Getter
	@Accessors(fluent = true)
	private boolean hasPrefix;
	@Getter
	@Accessors(fluent = true)
	private boolean isStaff;
	@Getter
	private Color color;

	Rank(String format, boolean hasPrefix, boolean isStaff) {
		this.format = format;
		this.hasPrefix = hasPrefix;
		this.isStaff = isStaff;
	}

	public String getPrefix() {
		if (hasPrefix) {
			return "&8&l[" + toString() + "&8&l]";
		}

		return "";
	}

	public String withFormat() {
		return format + StringUtils.camelCase(name());
	}

	public String withColor() {
		return getChatColor() + StringUtils.camelCase(name());
	}

	public String plain() {
		return StringUtils.camelCase(name());
	}

	public String getChatColor() {
		return format.replaceAll("&[lo]", "");
	}

	public List<Nerd> getNerds() {
		Set<PermissionUser> users = PermissionsEx.getPermissionManager().getGroup(StringUtils.camelCase(name())).getUsers();
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

	public static Rank getByString(String input) {
		try {
			return Rank.valueOf(input.toUpperCase());
		} catch (IllegalArgumentException missing) {
			switch (input.toLowerCase()) {
				case "administrator":
					return Rank.ADMIN;
				case "op":
					return Rank.OPERATOR;
				case "mod":
					return Rank.MODERATOR;
				case "arch":
					return Rank.ARCHITECT;
				case "vet":
					return Rank.VETERAN;
			}
		}
		return null;
	}

	public enum Group {
		ADMINS,
		SENIOR_STAFF,
		STAFF,
		BUILDERS,
		PLAYERS;
	}

}
