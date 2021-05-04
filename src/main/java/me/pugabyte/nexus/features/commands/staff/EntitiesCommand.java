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
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static me.pugabyte.nexus.utils.EntityUtils.getNearbyEntities;
import static me.pugabyte.nexus.utils.EntityUtils.getNearbyEntityTypes;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.Utils.sortByValue;

@Permission("group.staff")
public class EntitiesCommand extends CustomCommand {

	public EntitiesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("near [radius]")
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

	@Path("byChunk <type> [world]")
	void count(EntityType type, @Arg("current") World world) {
		sortByValue(new HashMap<Chunk, Integer>() {{
			for (Entity entity : world.getEntities()) {
				if (entity.getType() == type)
					put(entity.getChunk(), getOrDefault(entity.getChunk(), 0) + 1);
			}
		}}).forEach((chunk, count) -> {
			if (count > 0)
				send(getChunkMessage(chunk, count));
		});
	}

	@Path("villagers [world]")
	void villagers(@Arg("current") World world) {
		HashMap<Chunk, Villager> map = new HashMap<>() {{
			for (Entity entity : world.getEntities())
				if (entity.getType() == EntityType.VILLAGER)
					put(entity.getChunk(), (Villager) entity);
		}};

		HashMap<Chunk, Integer> noBoth = new HashMap<>();
		HashMap<Chunk, Integer> noProf = new HashMap<>();
		HashMap<Chunk, Integer> noBeds = new HashMap<>();

		map.forEach((chunk, villager) -> {
			boolean prof = true;
			boolean bed = true;
			if (villager.getProfession() == Profession.NONE)
				prof = false;
			if (world.getTime() >= 12541 && world.getTime() <= 23458 && !villager.isSleeping())
				bed = false;

			if (!prof && !bed)
				noBoth.put(chunk, noBoth.getOrDefault(chunk, 0) + 1);
			else {
				if (!prof)
					noProf.put(chunk, noProf.getOrDefault(chunk, 0) + 1);
				if (!bed)
					noBeds.put(chunk, noBeds.getOrDefault(chunk, 0) + 1);
			}
		});

		send(PREFIX + "No profession & no bed");
		noBoth.forEach((chunk, count) -> { if (count > 0) send(getChunkMessage(chunk, count)); });
		line();

		send(PREFIX + "No profession");
		noProf.forEach((chunk, count) -> { if (count > 0) send(getChunkMessage(chunk, count)); });
		line();

		send(PREFIX + "No bed");
		noBeds.forEach((chunk, count) -> { if (count > 0) send(getChunkMessage(chunk, count)); });
		line();
	}

	private JsonBuilder getChunkMessage(Chunk chunk, Integer count) {
		return json("&e" + chunk.getX() + ", " + chunk.getZ() + " &7- " + count).command("/tppos " + (chunk.getX() * 16) + " 100 " + (chunk.getZ() * 16) + " " + chunk.getWorld().getName());
	}

}
