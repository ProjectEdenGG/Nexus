package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HealthCommand extends CustomCommand {

	public HealthCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player] [number]")
	void health(@Arg("self") Player player, @Arg Double health) {
		if (health == null)
			send(PREFIX + player.getName() + "'s health is " + player.getHealth());
		else {
			checkPermission("health.set");
			player.setHealth(health);
			send(PREFIX + ChatColor.stripColor(player.getName()) + "'s health set to " + player.getHealth());
		}
	}

	@Path("target [number]")
	void target(@Arg Double health) {
		LivingEntity targetEntity = Utils.getTargetEntity(player());
		if (health == null)
			send(PREFIX + ChatColor.stripColor(targetEntity.getName()) + "'s health is " + targetEntity.getHealth());
		else {
			checkPermission("health.set");
			targetEntity.setHealth(health);
			send(PREFIX + ChatColor.stripColor(targetEntity.getName()) + "'s health set to " + targetEntity.getHealth());
		}
	}
}
