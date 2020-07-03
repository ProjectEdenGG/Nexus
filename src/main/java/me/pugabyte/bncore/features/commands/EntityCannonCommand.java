package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
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
		final Entity entity = player().getWorld().spawnEntity(player().getEyeLocation(), type);
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
