package gg.projecteden.nexus.utils;

import lombok.experimental.UtilityClass;
import me.lexikiq.HasOfflinePlayer;
import me.lexikiq.HasPlayer;
import me.lexikiq.HasUniqueId;
import me.lexikiq.OptionalPlayerLike;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Name {
	private static final Map<UUID, String> NAME_CACHE = new HashMap<>();

	public @Nullable String of(@NotNull UUID uuid) {
		if (!NAME_CACHE.containsKey(uuid)) {
			String name = Bukkit.getOfflinePlayer(uuid).getName();
			if (name != null)
				NAME_CACHE.put(uuid, name);
		}
		return NAME_CACHE.get(uuid);
	}

	public @Nullable String of(@NotNull HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public @Nullable String of(@NotNull OfflinePlayer player) {
		if (player instanceof Player online)
			return of(online);

		UUID uuid = player.getUniqueId();
		if (!NAME_CACHE.containsKey(uuid)) {
			String name = player.getName();
			if (name != null)
				NAME_CACHE.put(uuid, name);
		}
		return NAME_CACHE.get(player.getUniqueId());
	}

	public @Nullable String of(@NotNull HasOfflinePlayer hasPlayer) {
		if (hasPlayer instanceof Player online)
			return of(online);

		return of(hasPlayer.getOfflinePlayer());
	}

	public @NotNull String of(@NotNull Player player) {
		return put(player.getUniqueId(), player.getName());
	}

	public @NotNull String of(@NotNull HasPlayer hasPlayer) {
		return of(hasPlayer.getPlayer());
	}

	public @Nullable String of(@NotNull OptionalPlayerLike playerLike) {
		Player player = playerLike.getPlayer();
		if (player != null)
			return of(player);
		else
			return of(playerLike.getOfflinePlayer());
	}

	public @NotNull String put(@NotNull UUID uuid, @NotNull String name) {
		NAME_CACHE.put(uuid, name);
		return name;
	}
}
