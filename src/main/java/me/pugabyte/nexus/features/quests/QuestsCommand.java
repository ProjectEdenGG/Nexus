package me.pugabyte.nexus.features.quests;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;

@Permission("group.admin")
public class QuestsCommand extends CustomCommand {

	public QuestsCommand(CommandEvent event) {
		super(event);
	}

	@Path("mobheads debug")
	void mobheadsDebug() {
		List<EntityType> types = Arrays.asList(EntityType.values());
		for (EntityType entityType : MobHeads.getMobHeads().keySet()) {
			Class<? extends Entity> entity = entityType.getEntityClass();
			if (entity != null && LivingEntity.class.isAssignableFrom(entity) && !types.contains(entityType))
				send("Mob Head not found: " + StringUtils.camelCase(entityType));
		}
	}
}
