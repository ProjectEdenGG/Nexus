package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PlotUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
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

@Permission(Group.STAFF)
@Description("Shows all nearby entities")
public class EntitiesCommand extends CustomCommand {

	public EntitiesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("plot")
	@Description("Count all entities within a plot")
	void plot() {
		var plot = PlotUtils.getPlot(location());
		if (plot == null)
			error("No plot found");

		int count = 0;
		for (int plotEntities : plot.countEntities()) {
			count += plotEntities;
		}

		send("&3Total: &e" + count);
	}

	@Path("near [radius]")
	@Description("Count all entities within a radius")
	void run(@Arg("200") int radius) {
		line();
		send(PREFIX + "Found:");
		LinkedHashMap<EntityType, Long> nearbyEntities = EntityUtils.getNearbyEntityTypes(location(), radius);
		nearbyEntities.forEach((entityType, count) -> send("&e" + StringUtils.camelCase(entityType.name()) + " &7- " + count));
		send("");
		send("&3Total: &e" + nearbyEntities.values().stream().mapToLong(i -> i).sum());
	}

	@Path("find <type> [radius] [--uuid]")
	@Description("Locate all nearby entities of a specific type")
	void find(EntityType type, @Arg("200") int radius, @Switch @Arg("false") boolean uuid) {
		var entityMap = EntityUtils.getNearbyEntities(location(), radius);
		entityMap.forEach((entity, count) -> {
			if (entity.getType() != type)
				return;

			Location location = entity.getLocation();

			String name = StringUtils.camelCase(entity.getType());
			if (!Nullables.isNullOrEmpty(entity.getCustomName()))
				name = name + " named " + StringUtils.stripColor(entity.getCustomName());

			JsonBuilder json = new JsonBuilder("&7 - &e" + name).hover("Click to TP")
				.command(StringUtils.getTeleportCommandFloored(location)).group();

			if (uuid) {
				String entityUUID = entity.getUniqueId().toString();
				json.next("&3 - &e").group().next(entityUUID).hover("Click to insert").suggest(entityUUID).group();
			}

			json.send(player());
		});

		send();
		send(PREFIX + entityMap.size() + " found in radius of " + radius);
	}

	@Path("report [radius] [type]")
	@Description("View the number of entities near each online player")
	void report(@Arg("200") int radius, EntityType type) {
		Utils.sortByValue(new HashMap<Player, Integer>() {{
			for (Player player : OnlinePlayers.getAll())
				put(player, player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, entity -> type == null || type == entity.getType()).size());
		}}).forEach((player, count) -> {
			if (count > 0)
				send(json("&e" + player.getName() + " &7- " + count).command("/tp " + player.getName()));
		});
	}

	@Path("byChunk <type> [world]")
	@Description("View entity counts by chunk")
	void count(EntityType type, @Arg("current") World world) {
		Utils.sortByValue(new HashMap<Chunk, Integer>() {{
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
	@Description("View villager counts and whether they have a bed & profession by chunk")
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
		return json("&e" + chunk.getX() + ", " + chunk.getZ() + " &7- " + count)
			.command("/tppos " + (chunk.getX() * 16) + " 100 " + (chunk.getZ() * 16) + " " + chunk.getWorld().getName());
	}

}
