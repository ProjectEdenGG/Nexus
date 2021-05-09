package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Time;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@Permission("entity.cannon")
@Redirect(from = "/pigpistol", to = "/entitycannon pig")
@Redirect(from = "/kittycannon", to = "/entitycannon cat")
@Redirect(from = {"/beezooka", "/bz"}, to = "/entitycannon bee")
public class EntityCannonCommand extends CustomCommand {

	public EntityCannonCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[entityType]")
	void run(EntityType type) {
		if (!isSeniorStaff() && type == EntityType.THROWN_EXP_BOTTLE)
			permissionError();

		final Entity entity = world().spawnEntity(player().getEyeLocation(), type);
		entity.setInvulnerable(true);

		if (type.getEntityClass() != null && type.getEntityClass().isInstance(LivingEntity.class))
			((LivingEntity) entity).setAI(false);

		entity.setVelocity(player().getEyeLocation().getDirection().multiply(2));

		Tasks.wait(Time.SECOND, () -> {
			final Location loc = entity.getLocation();
			entity.remove();
			loc.getWorld().createExplosion(loc, 0F);
		});
	}
}
