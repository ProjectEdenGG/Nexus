package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases("mcmd")
@Permission(Group.STAFF)
public class MultiCommandCommand extends CustomCommand {

	public MultiCommandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Run multiple commands at once separated by ' ;; '")
	void run(@Vararg String input) {
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
