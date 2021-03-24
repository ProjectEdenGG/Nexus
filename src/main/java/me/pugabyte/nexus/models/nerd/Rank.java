package me.pugabyte.nexus.models.nerd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.toHex;

@AllArgsConstructor
public enum Rank {
	GUEST(ChatColor.of("#aaaaaa"), GlowAPI.Color.GRAY, false, false, false, true),
	MEMBER(ChatColor.of("#ffffff"), GlowAPI.Color.WHITE, false, false, false, true),
	TRUSTED(ChatColor.of("#ff7069"), GlowAPI.Color.RED, false, false, false, false, true, Color.decode("#ff7069")),
	ELITE(ChatColor.of("#f5a138"), GlowAPI.Color.GOLD, false, false, false, false, true, Color.decode("#f5a138")),
	VETERAN(ChatColor.of("#ffff44"), GlowAPI.Color.YELLOW, true, false, false, false, true, Color.decode("#ffff44")),
	BUILDER(ChatColor.of("#02883e"), GlowAPI.Color.GREEN, true, true, false, false, true, Color.decode("#02883e")),
	ARCHITECT(ChatColor.of("#02c93e"), GlowAPI.Color.DARK_GREEN, true, true, false, false, true, Color.decode("#02c93e")),
	MINIGAME_MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA, true, true, false, false, false, Color.decode("#4cc9f0")),
	MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA, true, true, true, false, true, Color.decode("#4cc9f0")),
	OPERATOR(ChatColor.of("#07a8a8"), GlowAPI.Color.DARK_AQUA, true, true, true, true, true, Color.decode("#07a8a8")),
	ADMIN(ChatColor.of("#3080ff"), GlowAPI.Color.BLUE, true, true, true, true, true, Color.decode("#1687d3")),
	OWNER(ChatColor.of("#915bf5"), GlowAPI.Color.DARK_PURPLE, true, true, true, true, true, Color.decode("#915bf5"));

	@Getter
	private final ChatColor color;
	@Getter
	private final GlowAPI.Color glowColor;
	@Getter
	@Accessors(fluent = true)
	private final boolean hasPrefix;
	@Getter
	@Accessors(fluent = true)
	private final boolean isStaff;
	@Getter
	@Accessors(fluent = true)
	private final boolean isMod;
	@Getter
	@Accessors(fluent = true)
	private boolean isSeniorStaff;
	@Getter
	@Accessors(fluent = true)
	private final boolean isActive;
	@Getter
	private Color discordColor;

	Rank(ChatColor color, GlowAPI.Color glowColor, boolean hasPrefix, boolean isStaff, boolean isMod, boolean isActive) {
		this.color = color;
		this.glowColor = glowColor;
		this.hasPrefix = hasPrefix;
		this.isStaff = isStaff;
		this.isMod = isMod;
		this.isActive = isActive;
	}

	public String getPrefix() {
		if (hasPrefix)
			return withColor();

		return "";
	}

	public String withColor() {
		return color + StringUtils.camelCase(name());
	}

	public String getHex() {
		return "&" + toHex(color);
	}

	public String plain() {
		return StringUtils.camelCase(name());
	}

	public List<Nerd> getNerds() {
		// Temporary? fix to get players in this group. Using Hours Top limit 100 because this method is only used for staff
		List<OfflinePlayer> inGroup = new HoursService().getActivePlayers().stream()
				.filter(player -> Nexus.getPerms().playerHas(null, player, "rank." + name().toLowerCase()))
				.collect(Collectors.toList());
		Set<Nerd> nerds = new HashSet<>();
		inGroup.forEach(player -> nerds.add(new NerdService().get(player)));
		return new ArrayList<>(nerds);
	}

	public List<Nerd> getOnlineNerds() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank() == this)
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static List<Rank> getStaff() {
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineStaff() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isStaff() && Nerd.of(player).getRank().isActive())
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static List<Rank> getMods() {
		return Arrays.stream(Rank.values()).filter(Rank::isMod).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineMods() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isMod() && Nerd.of(player).getRank().isActive())
				.sorted(Comparator.comparing(Player::getName))
				.map(player -> (Nerd) new NerdService().get(player))
				.collect(Collectors.toList());
	}

	public static Rank of(Player player) {
		return of(Bukkit.getOfflinePlayer(player.getUniqueId()));
	}

	public static Rank of(OfflinePlayer player) {
		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		for (Rank rank : ranks)
			if (Nexus.getPerms().playerInGroup(null, player, rank.name()))
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

	public boolean gte(Rank rank) {
		return ordinal() >= rank.ordinal();
	}

	public boolean lt(Rank rank) {
		return ordinal() < rank.ordinal();
	}

	public Rank next() {
		return EnumUtils.next(Rank.class, this.ordinal());
	}

	public Rank previous() {
		return EnumUtils.previous(Rank.class, this.ordinal());
	}

}
