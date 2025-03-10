package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@Permission(Group.STAFF)
@Redirect(from = "/pigpistol", to = "/entitycannon pig")
@Redirect(from = "/kittycannon", to = "/entitycannon cat")
@Redirect(from = {"/beezooka", "/bz"}, to = "/entitycannon bee")
public class EntityCannonCommand extends CustomCommand {

	public EntityCannonCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[entityType]")
	@Description("Launch an exploding entity")
	void run(EntityType type) {
		if (!isSeniorStaff() && type == EntityType.EXPERIENCE_BOTTLE)
			permissionError();

		final Entity entity = world().spawnEntity(player().getEyeLocation(), type);
		entity.setInvulnerable(true);

		if (type.getEntityClass() != null && type.getEntityClass().isInstance(LivingEntity.class))
			((LivingEntity) entity).setAI(false);

		entity.setVelocity(player().getEyeLocation().getDirection().multiply(2));

		Tasks.wait(TickTime.SECOND, () -> {
			final Location loc = entity.getLocation();
			entity.remove();
			loc.getWorld().createExplosion(loc, 0F);
		});
	}
}
