package me.pugabyte.bncore.features.store.perks.fireworks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.Time;

@Aliases("fw")
@Permission("firework.launch")
@Cooldown(value = @Part(Time.SECOND), bypass = "group.staff")
public class FireworkCommand extends CustomCommand {

	public FireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		FireworkLauncher.random(player().getLocation()).launch();
	}
}
