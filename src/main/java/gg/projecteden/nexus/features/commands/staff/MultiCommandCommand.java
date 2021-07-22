package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases("mcmd")
@Permission("group.staff")
public class MultiCommandCommand extends CustomCommand {

	public MultiCommandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<commands...>")
	void run(String input) {
		runMultiCommand(input.split(" ;; "));
	}

	public static void run(CommandSender sender, List<String> commands) {
		if (commands.size() == 0)
			return;

		AtomicInteger wait = new AtomicInteger(0);
		commands.forEach(command -> {
			if (command.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(command.toLowerCase().replace("wait ", "")));
			else
				Tasks.wait(wait.getAndAdd(3), () -> PlayerUtils.runCommand(sender, command));
		});
	}

}
