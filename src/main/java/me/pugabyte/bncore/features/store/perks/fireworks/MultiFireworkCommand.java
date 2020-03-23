package me.pugabyte.bncore.features.store.perks.fireworks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;

@Aliases("multifw")
@Permission("firework.launch")
@Cooldown(value = @Part(value = Time.SECOND, x = 10), bypass = "group.staff")
public class MultiFireworkCommand extends CustomCommand {

	public MultiFireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		Tasks.Countdown.builder()
				.duration(20 * 20)
				.onSecond(i -> {
					if (i % 2 == 0)
						FireworkLauncher.random(player().getLocation()).launch();
				})
				.doZero(true)
				.start();
	}
}
