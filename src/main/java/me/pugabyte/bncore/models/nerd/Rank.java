package me.pugabyte.bncore.models.nerd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
	GUEST("&#aaaaaa", false, false, false, true),
	MEMBER("&#ffffff", false, false, false, true),
	TRUSTED("&#ff7069", false, false, false, false, true, Color.decode("#ff7069")),
	ELITE("&#f5a138", false, false, false, false, true, Color.decode("#f5a138")),
	VETERAN("&#ffff44", true, false, false, false, true, Color.decode("#ffff44")),
	BUILDER("&#02883e", true, true, false, false, true, Color.decode("#02883e")),
	ARCHITECT("&#02c93e", true, true, false, false, true, Color.decode("#02c93e")),
	MINIGAME_MODERATOR("&#4cc9f0", true, true, false, false, false, Color.decode("#4cc9f0")),
	MODERATOR("&#4cc9f0", true, true, true, false, true, Color.decode("#4cc9f0")),
	OPERATOR("&#07a8a8", true, true, true, true, true, Color.decode("#07a8a8")),
	ADMIN("&#3080ff", true, true, true, true, true, Color.decode("#1687d3")),
	OWNER("&#915bf5", true, true, true, true, true, Color.decode("#915bf5"));

	@Getter
	private String format;
	@Getter
	@Accessors(fluent = true)
	private boolean hasPrefix;
	@Getter
	@Accessors(fluent = true)
	private boolean isStaff;
	@Getter
	@Accessors(fluent = true)
	private boolean isMod;
	@Getter
	@Accessors(fluent = true)
	private boolean isSeniorStaff;
	@Getter
	@Accessors(fluent = true)
	private boolean isActive;
	@Getter
	private Color discordColor;

	Rank(String format, boolean hasPrefix, boolean isStaff, boolean isMod, boolean isActive) {
		this.format = format;
		this.hasPrefix = hasPrefix;
		this.isStaff = isStaff;
		this.isMod = isMod;
		this.isActive = isActive;
	}

	public String getPrefix() {
		if (hasPrefix)
			return withFormat();

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
		// Temporary? fix to get players in this group. Using Hours Top limit 100 because this method is only used for staff
		List<OfflinePlayer> inGroup = new HoursService().getActivePlayers().stream()
				.filter(player -> BNCore.getPerms().playerHas(null, player, "rank." + name().toLowerCase()))
				.collect(Collectors.toList());
		Set<Nerd> nerds = new HashSet<>();
		inGroup.forEach(player -> nerds.add(new NerdService().get(player)));
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
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineStaff() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isStaff() && new Nerd(player).getRank().isActive())
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static List<Rank> getMods() {
		return Arrays.stream(Rank.values()).filter(Rank::isMod).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineMods() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> new Nerd(player).getRank().isMod() && new Nerd(player).getRank().isActive())
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static Rank getHighestRank(Player player) {
		return getHighestRank(Bukkit.getOfflinePlayer(player.getUniqueId()));
	}

	public static Rank getHighestRank(OfflinePlayer player) {
		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		for (Rank rank : ranks)
			if (BNCore.getPerms().playerInGroup(null, player, rank.name()))
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
		PLAYERS
	}

	public boolean includes(Rank rank) {
		return ordinal() >= rank.ordinal();
	}

	public Rank next() {
		return EnumUtils.next(Rank.class, this.ordinal());
	}

	public Rank previous() {
		return EnumUtils.previous(Rank.class, this.ordinal());
	}

}
