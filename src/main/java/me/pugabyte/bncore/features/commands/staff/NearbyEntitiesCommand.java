package me.pugabyte.bncore.features.commands.staff;

import com.google.common.base.Strings;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.Utils.getNearbyEntities;
import static me.pugabyte.bncore.utils.Utils.getNearbyEntityTypes;
import static me.pugabyte.bncore.utils.Utils.sort;

@Permission("group.staff")
public class NearbyEntitiesCommand extends CustomCommand {

	public NearbyEntitiesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[radius]")
	void run(@Arg("200") int radius) {
		line();
		send(PREFIX + "Found:");
		LinkedHashMap<EntityType, Long> nearbyEntities = getNearbyEntityTypes(player().getLocation(), radius);
		nearbyEntities.forEach((entityType, count) -> send("&e" + StringUtils.camelCase(entityType.name()) + " &7- " + count));
		send("");
		send("&3Total: &e" + nearbyEntities.values().stream().mapToLong(i -> i).sum());
	}

	@Path("find <type> [radius]")
	void find(EntityType type, @Arg("200") int radius) {
		getNearbyEntities(player().getLocation(), radius).entrySet().stream()
				.filter(entry -> entry.getKey().getType() == type)
				.forEach(entry -> {
					Entity entity = entry.getKey();
					Location location = entity.getLocation();

					String name = StringUtils.camelCase(entity.getType().name());
					if (!Strings.isNullOrEmpty(entity.getCustomName()))
						name = name + " named " + stripColor(entity.getCustomName());

					new JsonBuilder("&e" + name)
							.command("/tppos " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ())
							.send(player());
				});
	}

	@Path("report [radius]")
	void report(@Arg("200") int radius) {
		sort(new HashMap<Player, Integer>() {{
			for (Player player : Bukkit.getOnlinePlayers())
				put(player, player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).size());
		}}).forEach((player, count) ->
				send(json("&e" + player.getName() + " &7- " + count).command("/tp " + player.getName())));
	}

	@Path("villagers")
	void report() {
		sort(new HashMap<Player, Integer>() {{
			for (Player player : Bukkit.getOnlinePlayers())
				put(player, (int) player.getWorld().getNearbyEntities(player.getLocation(), 200, 200, 200).stream()
						.filter(entity -> entity.getType() == EntityType.VILLAGER).count());
		}}).forEach((player, count) ->
				send(json("&e" + player.getName() + " &7- " + count).command("/tp " + player.getName())));
	}

}
