package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
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

import static gg.projecteden.nexus.utils.EntityUtils.getNearbyEntities;
import static gg.projecteden.nexus.utils.EntityUtils.getNearbyEntityTypes;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.nexus.utils.Utils.sortByValue;

@Permission(Group.STAFF)
@Description("Shows all nearby entities")
public class EntitiesCommand extends CustomCommand {

	public EntitiesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("Count all entities within a radius")
	void near(@Optional("200") int radius) {
		line();
		send(PREFIX + "Found:");
		LinkedHashMap<EntityType, Long> nearbyEntities = getNearbyEntityTypes(location(), radius);
		nearbyEntities.forEach((entityType, count) -> send("&e" + StringUtils.camelCase(entityType.name()) + " &7- " + count));
		send("");
		send("&3Total: &e" + nearbyEntities.values().stream().mapToLong(i -> i).sum());
	}

	@Description("Locate all nearby entities of a specific type")
	void find(EntityType type, @Optional("200") int radius) {
		getNearbyEntities(location(), radius).forEach((entity, count) -> {
			if (entity.getType() != type)
				return;

			Location location = entity.getLocation();

			String name = StringUtils.camelCase(entity.getType().name());
			if (!Nullables.isNullOrEmpty(entity.getCustomName()))
				name = name + " named " + stripColor(entity.getCustomName());

			new JsonBuilder("&e" + name)
					.command("/tppos " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ())
					.send(player());
		});
	}

	@Description("View the number of entities near each online player")
	void report(@Optional("200") int radius, @Optional EntityType type) {
		sortByValue(new HashMap<Player, Integer>() {{
			for (Player player : OnlinePlayers.getAll())
				put(player, player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, entity -> type == null || type == entity.getType()).size());
		}}).forEach((player, count) -> {
			if (count > 0)
				send(json("&e" + player.getName() + " &7- " + count).command("/tp " + player.getName()));
		});
	}

	@Description("View entity counts by chunk")
	void byChunk(EntityType type, @Optional("current") World world) {
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

	@Description("View villager counts and whether they have a bed & profession by chunk")
	void villagers(@Optional("current") World world) {
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
