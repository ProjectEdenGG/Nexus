package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.event.Listener;

@Aliases("ap")
@NoArgsConstructor
@Permission("group.staff")
public class AeveonProjectCommand extends CustomCommand implements Listener {

	public AeveonProjectCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("AP");
	}

	@Path()
	public void run() {
		send(PREFIX + "TODO");
	}

	@Path("warps [string...]")
	public void warps(String arguments) {
		if (isNullOrEmpty(arguments))
			arguments = "";
		else
			arguments = " " + arguments;
		runCommand("aeveonprojectwarps" + arguments);
	}

}
