package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static me.pugabyte.nexus.utils.WorldUtils.getRandomLocationInBorder;

/**
 * Represents a mechanic which takes place in a separate world
 * and plays like a vanilla survival game
 * @param <T> what object should be used to spread players
 */
public interface VanillaMechanic<T> {
	@NotNull
	default World getWorld() {
		World world = Bukkit.getWorld(getWorldName());
		if (world == null)
			throw new MinigameException(getClass().getName() + " world not created");
		return world;
	}

	@NotNull
	String getWorldName();

	default boolean canOpenInventoryBlocks() {
		return true;
	}

	default boolean canDropItem(@NotNull ItemStack item) {
		return true;
	}

	void spreadPlayers(@NotNull Match match);

	default void randomTeleport(@NotNull Match match, @NotNull T t) {
		Location random = getRandomLocationInBorder(getWorld());
		PaperLib.getChunkAtAsync(random, true).thenRun(() -> {
			Location location = getWorld().getHighestBlockAt(random).getLocation();
			if (location.getBlock().getType().isSolid())
				onRandomTeleport(match, t, location.add(0, 1, 0));
			else
				randomTeleport(match, t);
		});
	}

	@NotNull CompletableFuture<Void> onRandomTeleport(@NotNull Match match, @NotNull T t, @NotNull Location location);

	int getWorldRadius();

	default void setWorldBorder(double x, double z) {
		setWorldBorder(new Location(getWorld(), x, 0, z));
	}

	default void setWorldBorder(@NotNull Location center) {
		WorldBorder border = getWorld().getWorldBorder();
		border.setCenter(center);
		border.setSize(getWorldRadius());
		border.setDamageAmount(0.2);
		border.setWarningDistance(20);
	}
	
	default void onStart(@NotNull MatchStartEvent event) {
		getWorld().setTime(0);
		getWorld().getEntities().forEach(entity -> {
			if (!(entity instanceof HumanEntity))
				entity.remove();
		});

		int worldRadius = getWorldRadius();
		setWorldBorder(RandomUtils.randomInt(-worldRadius, worldRadius), RandomUtils.randomInt(-worldRadius, worldRadius));

		event.getMatch().getTasks().wait(1, () -> spreadPlayers(event.getMatch()));
	}

	default void resetBorder() {
		getWorld().getWorldBorder().reset();
	}
}
