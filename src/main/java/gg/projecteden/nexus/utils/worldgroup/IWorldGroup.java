package gg.projecteden.nexus.utils.worldgroup;

import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface IWorldGroup {

		String name();

		List<String> getWorldNames();

		default boolean contains(World world) {
			return contains(world.getName());
		}

		default boolean contains(String world) {
			return getWorldNames().contains(world);
		}

		default List<World> getWorlds() {
			return getWorldNames().stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList());
		}

		default List<Player> getPlayers() {
			return getWorlds().stream().map(world -> OnlinePlayers.where().world(world).get()).flatMap(Collection::stream).toList();
		}

	}
