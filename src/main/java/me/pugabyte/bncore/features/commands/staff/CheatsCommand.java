package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.godmode.Godmode;
import me.pugabyte.bncore.models.godmode.GodmodeService;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.GameMode;

@Permission("group.staff")
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand {

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<on|off>")
	void on(Boolean enabled) {
		GodmodeService godmodeService = new GodmodeService();
		Godmode godmode = godmodeService.get(player());
		if (enabled) {
			if (player().hasPermission("essentials.gamemode.creative"))
				player().setGameMode(GameMode.CREATIVE);
			player().setAllowFlight(true);
			player().setFlying(true);
			godmode.setEnabled(true);
			godmodeService.save(godmode);
			runCommand("vanish on");

			send(PREFIX + "&aEnabled");
		} else {
			runCommand("vanish off");
			godmode.setEnabled(false);
			godmodeService.save(godmode);
			if (WorldGroup.get(player()) != WorldGroup.CREATIVE)
				player().setGameMode(GameMode.SURVIVAL);
			player().setFallDistance(0);
			player().setAllowFlight(false);
			player().setFlying(false);

			send(PREFIX + "&cDisabled");
		}
	}
}
