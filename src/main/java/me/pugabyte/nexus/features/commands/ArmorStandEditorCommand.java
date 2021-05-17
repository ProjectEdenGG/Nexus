package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import static me.pugabyte.nexus.features.listeners.Restrictions.isPerkAllowedAt;

@Aliases({"ase", "armourstandeditor"})
public class ArmorStandEditorCommand extends CustomCommand {

	public ArmorStandEditorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("arms [enable]")
	void arms(Boolean enabled) {
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(armorStand.getLocation()))
			error("You cannot edit armor stands here");

		if (enabled == null)
			enabled = !armorStand.hasArms();

		armorStand.setArms(enabled);
		send(PREFIX + "Arms " + (enabled ? "&aenabled" : "&cdisabled"));
	}

}
