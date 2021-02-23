package me.pugabyte.nexus.features.commands.staff;

import com.google.common.base.Strings;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static me.pugabyte.nexus.utils.EntityUtils.getNearbyEntities;
import static me.pugabyte.nexus.utils.EntityUtils.getNearbyEntityTypes;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.Utils.sortByValue;

@Permission("group.staff")
public class NearbyEntitiesCommand extends CustomCommand {

	public NearbyEntitiesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[radius]")
	void run(@Arg("200") int radius) {
		line();
		send(PREFIX + "Found:");
		LinkedHashMap<EntityType, Long> nearbyEntities = getNearbyEntityTypes(location(), radius);
		nearbyEntities.forEach((entityType, count) -> send("&e" + StringUtils.camelCase(entityType.name()) + " &7- " + count));
		send("");
		send("&3Total: &e" + nearbyEntities.values().stream().mapToLong(i -> i).sum());
	}

	@Path("find <type> [radius]")
	void find(EntityType type, @Arg("200") int radius) {
		getNearbyEntities(location(), radius).forEach((entity, count) -> {
			if (entity.getType() != type)
				return;

			Location location = entity.getLocation();

			String name = StringUtils.camelCase(entity.getType().name());
			if (!Strings.isNullOrEmpty(entity.getCustomName()))
				name = name + " named " + stripColor(entity.getCustomName());

			new JsonBuilder("&e" + name)
					.command("/tppos " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ())
					.send(player());
		});
	}

	@Path("report [radius] [type]")
	void report(@Arg("200") int radius, EntityType type) {
		sortByValue(new HashMap<Player, Integer>() {{
			for (Player player : Bukkit.getOnlinePlayers())
				put(player, player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, entity -> type == null || type == entity.getType()).size());
		}}).forEach((player, count) -> {
			if (count > 0)
				send(json("&e" + player.getName() + " &7- " + count).command("/tp " + player.getName()));
		});
	}

	@Path("chunkCount [world] [type]")
	void count(World world, EntityType type) {
		sortByValue(new HashMap<Chunk, Integer>() {{
			for (Entity entity : world.getEntities()) {
				if (entity.getType() == type)
					put(entity.getChunk(), getOrDefault(entity.getChunk(), 0) + 1);
			}
		}}).forEach((chunk, count) -> {
			if (count > 0)
				send(json("&e" + chunk.getX() + ", " + chunk.getZ() + " &7- " + count).command("/tppos " + (chunk.getX() * 16) + " 100 " + (chunk.getZ() * 16)));
		});
	}

}
