package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@Permission("group.staff")
public class HealCommand extends CustomCommand {

	public HealCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		send(player, PREFIX + "You have been healed");
		if (!isSelf(player))
			send(PREFIX + player.getName() + " has been healed");
	}

}
