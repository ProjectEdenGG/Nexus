package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

@Aliases("petowner")
public class AnimalOwnerCommand extends CustomCommand {

	public AnimalOwnerCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		LivingEntity entity = getTargetLivingEntityRequired();
		if (!(entity instanceof Tameable))
			error("Entity cannot be tamed");
		Tameable tameable = (Tameable) entity;

		if (tameable.getOwner() == null)
			error("This "+tameable.getName()+" is not tamed");

		String name;
		if (tameable.getOwner().getName() != null)
			name = tameable.getOwner().getName();
		else
			name = tameable.getOwner().getUniqueId().toString();

		send(PREFIX + "That "+tameable.getName()+" is owned by "+name);
	}

}
