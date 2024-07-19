package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("<commands...> [--asOp]")
	@Description("Run multiple commands at once separated by ' ;; '")
	void run(String input, @Switch boolean asOp) {
		runMultiCommandAsOp(input.split(" ;; "));
	}

	public static void run(CommandSender sender, List<String> commands, boolean asOp) {
		if (commands.isEmpty())
			return;

		AtomicInteger wait = new AtomicInteger(0);
		commands.forEach(command -> {
			if (command.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(command.toLowerCase().replace("wait ", "")));
			else
				Tasks.wait(wait.getAndAdd(3), () -> {
					if (asOp)
						PlayerUtils.runCommandAsOp(sender, command);
					else
						PlayerUtils.runCommand(sender, command);
				});
		});
	}

}
