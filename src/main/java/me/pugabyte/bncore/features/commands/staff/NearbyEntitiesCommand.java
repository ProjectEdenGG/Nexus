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
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.Utils.getNearbyEntities;
import static me.pugabyte.bncore.utils.Utils.getNearbyEntityTypes;

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



}
