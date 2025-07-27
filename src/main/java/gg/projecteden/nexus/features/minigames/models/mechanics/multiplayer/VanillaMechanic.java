package gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.exceptions.MinigameException;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Represents a mechanic which takes place in a separate world
 * and plays like a vanilla survival game
 * @param <T> what object should be used to spread players
 */
public interface VanillaMechanic<T> extends Listener {
	@NotNull
	default World getWorld() {
		World world = Bukkit.getWorld(getWorldName());
		if (world == null)
			throw new MinigameException(getClass().getName() + " world not created");
		return world;
	}

	@NotNull
	String getWorldName();

	void spreadPlayers(@NotNull Match match);

	default void randomTeleport(@NotNull Match match, @NotNull T t) {
		Location random = WorldUtils.getRandomLocationInBorder(getWorld());
		getWorld().getChunkAtAsync(random, true).thenRun(() -> {
			Location location = getWorld().getHighestBlockAt(random).getLocation();
			if (location.getBlock().getType().isSolid())
				onRandomTeleport(match, t, location.add(0, 1, 0));
			else
				randomTeleport(match, t);
		});
	}

	@NotNull CompletableFuture<?> onRandomTeleport(@NotNull Match match, @NotNull T t, @NotNull Location location);

	int getWorldDiameter();

	default void setWorldBorder(@NotNull Location center) {
		WorldBorder border = getWorld().getWorldBorder();
		border.setCenter(center);
		getWorld().setSpawnLocation(center);
		border.setSize(getWorldDiameter());
		border.setDamageAmount(0.2);
		border.setWarningDistance(20);
	}
	
	default void onStart(@NotNull MatchStartEvent event) {
		getWorld().setTime(0);
		getWorld().getEntities().forEach(entity -> {
			Location location = entity.getLocation();
			Region region = event.getMatch().getArena().getRegion();
			boolean isInSpawn = region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			if (!(entity instanceof HumanEntity) && !isInSpawn)
				entity.remove();
		});

		getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		getWorld().setGameRule(GameRule.DO_INSOMNIA, false);
		getWorld().setGameRule(GameRule.DO_PATROL_SPAWNING, false);

		final Supplier<Double> random = () -> RandomUtils.randomDouble(-25_000_000, 25_000_000);
		setWorldBorder(new Location(getWorld(), random.get(), 0, random.get()));

		event.getMatch().getTasks().wait(5, () -> spreadPlayers(event.getMatch()));
	}

	default void resetBorder() {
		getWorld().getWorldBorder().reset();
	}

	default void dropItems(Minigamer minigamer) {
		ItemStack[] contents = minigamer.getOnlinePlayer().getInventory().getContents();
		for (ItemStack item : contents) {
			if (item != null)
				minigamer.getOnlinePlayer().getWorld().dropItemNaturally(minigamer.getOnlinePlayer().getLocation(), item);
		}
	}

	default boolean allowLocalChat() {
		return false;
	}

	@NotNull String getPrefix();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	default void onLocalChat(PublicChatEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getChatter().getUuid());

		if (!minigamer.isPlaying())
			return;
		if (!(minigamer.getMatch().getMechanic() instanceof VanillaMechanic))
			return;

		if (allowLocalChat())
			return;
		if (!StaticChannel.LOCAL.getChannel().equals(event.getChannel()))
			return;

		event.setCancelled(true);
		event.getChatter().sendMessage(JsonBuilder.fromPrefix(getPrefix()).next("&cLocal chat is disabled in this minigame"));
	}
}
