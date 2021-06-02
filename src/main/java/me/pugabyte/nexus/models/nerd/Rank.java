package me.pugabyte.nexus.models.nerd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.lexikiq.HasOfflinePlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.framework.interfaces.IsColoredAndNamed;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.inventivetalent.glow.GlowAPI;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Rank implements IsColoredAndNamed {
	GUEST(ChatColor.of("#aaaaaa"), GlowAPI.Color.GRAY, false, false, false, true),
	MEMBER(ChatColor.of("#ffffff"), GlowAPI.Color.WHITE, false, false, false, true),
	TRUSTED(ChatColor.of("#ff7069"), GlowAPI.Color.RED, false, false, false, false, true, false, Color.decode("#ff7069")),
	ELITE(ChatColor.of("#f5a138"), GlowAPI.Color.GOLD, false, false, false, false, true, false, Color.decode("#f5a138")),
	VETERAN(ChatColor.of("#ffff44"), GlowAPI.Color.YELLOW, true, false, false, false, true, true, Color.decode("#ffff44")),
	NOBLE(ChatColor.of("#abd923"), GlowAPI.Color.YELLOW, false, false, false, false, true, false, Color.decode("#abd923")),
	BUILDER(ChatColor.of("#02883e"), GlowAPI.Color.DARK_GREEN, true, true, false, false, true, false, Color.decode("#02883e")),
	ARCHITECT(ChatColor.of("#02c93e"), GlowAPI.Color.GREEN, true, true, false, false, true, false, Color.decode("#02c93e")),
	MINIGAME_MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA, true, true, false, false, false, false, Color.decode("#4cc9f0")),
	MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA, true, true, true, false, true, false, Color.decode("#4cc9f0")),
	OPERATOR(ChatColor.of("#07a8a8"), GlowAPI.Color.DARK_AQUA, true, true, true, true, true, false, Color.decode("#07a8a8")),
	ADMIN(ChatColor.of("#3080ff"), GlowAPI.Color.BLUE, true, true, true, true, true, false, Color.decode("#1687d3")),
	OWNER(ChatColor.of("#915bf5"), GlowAPI.Color.DARK_PURPLE, true, true, true, true, true, false, Color.decode("#915bf5"));

	@Getter
	@NotNull
	private final ChatColor chatColor;
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
	private final boolean isSeniorStaff;
	@Getter
	@Accessors(fluent = true)
	private final boolean isActive;
	@Getter
	@Accessors(fluent = true)
	private final boolean skipsPromotion;
	@Getter
	private final Color discordColor;

	Rank(@NotNull ChatColor chatColor, GlowAPI.Color glowColor, boolean hasPrefix, boolean isStaff, boolean isMod, boolean isActive) {
		this.chatColor = chatColor;
		this.glowColor = glowColor;
		this.hasPrefix = hasPrefix;
		this.isStaff = isStaff;
		this.isMod = isMod;
		isSeniorStaff = false;
		this.isActive = isActive;
		skipsPromotion = false;
		discordColor = null;
	}

	@Override
	public @NotNull Colored colored() {
		return Colored.colored(chatColor);
	}

	public String getPrefix() {
		if (hasPrefix)
			return getColoredName();

		return "";
	}

	public @NotNull String getName() {
		return StringUtils.camelCase(name());
	}

	@SneakyThrows
	public List<Nerd> getNerds() {
		Group group = Nexus.getLuckPerms().getGroupManager().getGroup(name());

		if (group == null)
			throw new InvalidInputException("&cGroup " + name() +  " does not exist!");

		var matcher = NodeMatcher.key(InheritanceNode.builder(group).build());
		return Nerd.of(Nexus.getLuckPerms().getUserManager().searchAll(matcher).get().keySet());
	}

	public List<Nerd> getOnlineNerds() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank() == this)
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static List<Rank> getStaff() {
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineStaff() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isStaff() && Nerd.of(player).getRank().isActive())
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineMods() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isMod() && Nerd.of(player).getRank().isActive())
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static Rank of(UUID player) {
		return of(Bukkit.getOfflinePlayer(player));
	}

	public static Rank of(HasOfflinePlayer player) {
		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		for (Rank rank : ranks)
			if (Nexus.getPerms().playerInGroup(null, player.getOfflinePlayer(), rank.name()))
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

	public boolean isAdmin() {
		return this == ADMIN || this == OWNER;
	}

	public enum RankGroup {
		ADMINS,
		SENIOR_STAFF,
		STAFF,
		BUILDERS,
		PLAYERS
	}

	public boolean gt(Rank rank) {
		return ordinal() > rank.ordinal();
	}

	public boolean gte(Rank rank) {
		return ordinal() >= rank.ordinal();
	}

	public boolean lt(Rank rank) {
		return ordinal() < rank.ordinal();
	}

	public boolean lte(Rank rank) {
		return ordinal() <= rank.ordinal();
	}

	public boolean between(Rank lower, Rank upper) {
		if (lower.ordinal() == upper.ordinal())
			return ordinal() == lower.ordinal();
		if (lower.ordinal() > upper.ordinal()) {
			Rank temp = lower;
			lower = upper;
			upper = temp;
		}

		return gte(lower) && lte(upper);
	}

	public Rank next() {
		Rank next = EnumUtils.next(Rank.class, this.ordinal());
		if (next == this)
			return next;
		if (!next.isActive)
			next = next.next();
		return next;
	}

	public Rank previous() {
		Rank previous = EnumUtils.previous(Rank.class, this.ordinal());
		if (previous == this)
			return previous;
		if (!previous.isActive)
			previous = previous.previous();
		return previous;
	}

	public Rank getPromotion() {
		Rank next = next();
		if (next == this)
			return next;
		if (next.skipsPromotion())
			next = next.getPromotion();
		return next;
	}

	public int enabledOrdinal() {
		return Arrays.stream(Rank.values()).filter(Rank::isActive).toList().indexOf(this);
	}
}
