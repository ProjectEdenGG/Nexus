package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;

import java.util.Arrays;
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
		run(Arrays.asList(input.split(" ;; ")));
	}

	void run(final List<String> commands) {
		if (commands.size() == 0)
			return;

		AtomicInteger wait = new AtomicInteger(0);
		commands.forEach(command -> {
			if (command.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(command.toLowerCase().replace("wait ", "")));
			else
				Tasks.wait(wait.getAndAdd(3), () -> runCommand(command));
		});
	}

}
