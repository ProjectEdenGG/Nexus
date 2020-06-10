package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
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

		player.setFallDistance(0f);
		player.setAllowFlight(enable);

		if (!player.getAllowFlight())
			player.setFlying(false);

		send(player, PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
		if (!isSelf(player))
			send(PREFIX + "Fly " + (enable ? "&aenabled" : "&cdisabled") + " &3for &e" + player.getName());
	}

}
