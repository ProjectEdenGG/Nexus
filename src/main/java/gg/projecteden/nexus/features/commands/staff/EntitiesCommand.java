package gg.projecteden.nexus.features.commands.staff;

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
import kotlin.Pair;
import lombok.NonNull;
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
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

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
			if (!isNullOrEmpty(entity.getCustomName()))
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

	@Path("byChunk [type] [--page] [--excludedTypes]")
	@Description("View entity counts by type and chunk")
	void count(
		EntityType type,
		@Switch @Arg("1") int page,
		@Switch @Arg(type = EntityType.class) List<EntityType> excludedTypes
	) {
		var values = Utils.sortByValueReverse(new HashMap<Pair<Chunk, EntityType>, Integer>() {{
			for (World world : Bukkit.getWorlds())
				for (Entity entity : world.getEntities())
					if ((type == null || entity.getType() == type) && (isNullOrEmpty(excludedTypes) || !excludedTypes.contains(entity.getType())))
						put(new Pair<>(entity.getChunk(), entity.getType()), getOrDefault(new Pair<>(entity.getChunk(), entity.getType()), 0) + 1);
		}});

		if (values.isEmpty())
			error("No entities found");

		String typeString = type == null ? "" : type.name();
		String excludedTypesString = isNullOrEmpty(excludedTypes) ? "" : "--excludedTypes=" + excludedTypes.stream().map(EntityType::name).collect(Collectors.joining(","));

		send(PREFIX + "Entities by type and chunk");
		new Paginator<Pair<Chunk, EntityType>>()
			.values(values.keySet())
			.formatter((value, index) -> getChunkMessage(value, values.get(value)))
			.command("/entities byChunk %s %s --page=".formatted(typeString, excludedTypesString))
			.page(page)
			.send();
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

	private JsonBuilder getChunkMessage(Pair<Chunk, EntityType> pair, Integer count) {
		var chunk = pair.getFirst();
		var type = pair.getSecond();
		int x = chunk.getX() * 16;
		int z = chunk.getZ() * 16;
		var world = chunk.getWorld();

		return json("&e%s &3%s %d, %d &7- %d".formatted(camelCase(type), world.getName(), x, z, count))
			.command("/tppos %d 100 %d %s".formatted(x, z, world.getName()));
	}

}
