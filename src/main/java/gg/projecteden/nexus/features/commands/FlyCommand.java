package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Permission("essentials.fly")
public class FlyCommand extends CustomCommand {

	public FlyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enable] [player]")
	void run(Boolean enable, @Arg("self") Player player) {
		if (enable == null)
			enable = !player.getAllowFlight();

		player.setFallDistance(0);
		player.setAllowFlight(enable);

		if (!player.getAllowFlight())
			player.setFlying(false);

		send(player, PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
		if (!isSelf(player))
			send(PREFIX + "Fly " + (enable ? "&aenabled" : "&cdisabled") + " &3for &e" + player.getName());
	}

}
