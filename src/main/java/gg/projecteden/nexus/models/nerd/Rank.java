package gg.projecteden.nexus.models.nerd;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lexikiq.HasOfflinePlayer;
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
	GUEST(ChatColor.of("#aaaaaa"), GlowAPI.Color.GRAY),
	MEMBER(ChatColor.of("#ffffff"), GlowAPI.Color.WHITE),
	TRUSTED(ChatColor.of("#ff7069"), GlowAPI.Color.RED),
	ELITE(ChatColor.of("#f5a138"), GlowAPI.Color.GOLD),
	VETERAN(ChatColor.of("#ffff44"), GlowAPI.Color.YELLOW),
	NOBLE(ChatColor.of("#9de23d"), GlowAPI.Color.YELLOW),
	BUILDER(ChatColor.of("#02883e"), GlowAPI.Color.DARK_GREEN),
	ARCHITECT(ChatColor.of("#02c93e"), GlowAPI.Color.GREEN),
	MINIGAME_MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA),
	MODERATOR(ChatColor.of("#4cc9f0"), GlowAPI.Color.AQUA),
	OPERATOR(ChatColor.of("#07a8a8"), GlowAPI.Color.DARK_AQUA),
	ADMIN(ChatColor.of("#3080ff"), GlowAPI.Color.BLUE),
	OWNER(ChatColor.of("#915bf5"), GlowAPI.Color.DARK_PURPLE),
	;

	@Getter
	@NotNull
	private final ChatColor chatColor;
	@Getter
	private final GlowAPI.Color glowColor;

	public Color getDiscordColor() {
		if (lt(TRUSTED))
			return null;
		if (this == ADMIN)
			return Color.decode("#1687d3");

		return getChatColor().getColor();
	}

	public boolean isActive() {
		return this != MINIGAME_MODERATOR;
	}

	public boolean hasPrefix() {
		return isStaff();
	}

	public boolean isStaff() {
		return gte(Rank.BUILDER);
	}

	public boolean isBuilder() {
		return between(BUILDER, ARCHITECT);
	}

	public boolean isMod() {
		return gte(Rank.MODERATOR);
	}

	public boolean isSeniorStaff() {
		return gte(Rank.OPERATOR);
	}

	public boolean isAdmin() {
		return gte(Rank.ADMIN);
	}

	public boolean skipsPromotion() {
		return this == VETERAN;
	}

	@Override
	public @NotNull Colored colored() {
		return Colored.colored(chatColor);
	}

	public String getPrefix() {
		if (hasPrefix())
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
		return PlayerUtils.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank() == this)
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static List<Rank> getStaff() {
		return Arrays.stream(Rank.values()).filter(Rank::isStaff).filter(Rank::isActive).collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineStaff() {
		return PlayerUtils.getOnlinePlayers().stream()
				.filter(player -> Nerd.of(player).getRank().isStaff() && Nerd.of(player).getRank().isActive())
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineMods() {
		return PlayerUtils.getOnlinePlayers().stream()
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
		if (!next.isActive())
			next = next.next();
		return next;
	}

	public Rank previous() {
		Rank previous = EnumUtils.previous(Rank.class, this.ordinal());
		if (previous == this)
			return previous;
		if (!previous.isActive())
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
