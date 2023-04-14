package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@Permission(Group.STAFF)
public class HealCommand extends CustomCommand {

	public HealCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Fill a player's health, food, and saturation, extinguish fire, and clear potion effects")
	void run(@Optional("self") Nerd nerd) {
		HealCommand.healPlayer(nerd.getOnlinePlayer());
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
