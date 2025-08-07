package gg.projecteden.nexus.models.nerd;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Rank implements IsColoredAndNamed {
	GUEST(ChatColor.of("#aaaaaa"), ChatColor.GRAY),
	MEMBER(ChatColor.of("#ffffff"), ChatColor.WHITE),
	TRUSTED(ChatColor.of("#ff7069"), ChatColor.RED),
	ELITE(ChatColor.of("#f5a138"), ChatColor.GOLD),
	VETERAN(ChatColor.of("#ffff44"), ChatColor.YELLOW),
	BUILDER(ChatColor.of("#02883e"), ChatColor.DARK_GREEN),
	ARCHITECT(ChatColor.of("#02c93e"), ChatColor.GREEN),
	MODERATOR(ChatColor.of("#4cc9f0"), ChatColor.AQUA),
	OPERATOR(ChatColor.of("#07a8a8"), ChatColor.DARK_AQUA),
	ADMIN(ChatColor.of("#3080ff"), ChatColor.BLUE),
	OWNER(ChatColor.of("#915bf5"), ChatColor.DARK_PURPLE),
	;

	@Getter
	@NotNull
	private final ChatColor chatColor;
	@Getter
	private final ChatColor similarChatColor;

	public GlowColor getGlowColor() {
		return GlowColor.valueOf(similarChatColor.getName().toUpperCase());
	}

	public static boolean exists(String key) {
		try {
			Rank.valueOf(key.toUpperCase());
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	public Color getDiscordColor() {
		if (lt(TRUSTED))
			return null;
		if (this == ADMIN)
			return Color.decode("#1687d3");

		return getChatColor().getColor();
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
		return Colored.of(chatColor);
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
	public CompletableFuture<List<Nerd>> getNerds() {
		return LuckPermsUtils.getUsersInGroup(this).thenApply(Nerd::of);
	}

	public List<Nerd> getOnlineNerds() {
		return OnlinePlayers.getAll().stream()
				.filter(player -> Rank.of(player) == this)
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static final List<Rank> STAFF_RANKS = Arrays.stream(Rank.values())
		.filter(Rank::isStaff)
		.sorted(Comparator.reverseOrder())
		.collect(Collectors.toList());

	@NotNull
	public static CompletableFuture<Map<Rank, List<Nerd>>> getStaffNerds() {
		return CompletableFutures.allOf(new LinkedHashMap<Rank, CompletableFuture<List<Nerd>>>() {{
			STAFF_RANKS.forEach(rank -> put(rank, rank.getNerds()));
		}});
	}

	public static List<Nerd> getOnlineStaff() {
		return OnlinePlayers.staff().get().stream()
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static List<Nerd> getOnlineMods() {
		return OnlinePlayers.getAll().stream()
				.filter(player -> Rank.of(player).isMod())
				.map(Nerd::of)
				.sorted(Comparator.comparing(Nerd::getNickname))
				.collect(Collectors.toList());
	}

	public static final List<Rank> REVERSED = Utils.reverse(Arrays.asList(Rank.values()));

	public static final LoadingCache<UUID, Rank> CACHE = CacheBuilder.newBuilder()
		.expireAfterWrite(10, TimeUnit.SECONDS)
		.build(CacheLoader.from(uuid -> {
			for (Rank rank : REVERSED)
				if (LuckPermsUtils.hasGroup(uuid, rank.name().toLowerCase()))
					return rank;

			return GUEST;
		}));

	public static Rank of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static Rank of(UUID uuid) {
		try {
			return CACHE.get(uuid);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return GUEST;
		}
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
		return next;
	}

	public Rank previous() {
		Rank previous = EnumUtils.previous(Rank.class, this.ordinal());
		if (previous == this)
			return previous;
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
		return Arrays.stream(Rank.values()).toList().indexOf(this);
	}
}
