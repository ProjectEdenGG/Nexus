package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@Permission(Group.SENIOR_STAFF)
public class HealCommand extends CustomCommand {

	public HealCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Fill a player's health, food, and saturation, extinguish fire, and clear potion effects")
	void run(@Arg("self") Nerd nerd) {
		final Player player = nerd.getOnlinePlayer();

		if (isSelf(nerd))
			if (!CheatsCommand.canEnableCheats(player))
				error("You cannot use cheats in this world");

		HealCommand.healPlayer(player);
		send(nerd, PREFIX + "You have been healed");
		if (!isSelf(nerd))
			send(PREFIX + "&e" + nerd.getNickname() + " &3has been healed");
	}

	public static void healPlayer(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}

}
