package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aliases("mcmd")
@Permission("group.staff")
public class MultiCommandCommand extends CustomCommand {

	public MultiCommandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<commands...>")
	void run(String input) {
		run(new ArrayList<>(Arrays.asList(input.split(" ;; "))));
	}

	void run(final List<String> commands) {
		if (commands.size() == 0)
			return;

		String command = commands.get(0);
		if (command.toLowerCase().matches("^wait \\d+$")) {
			int wait = Integer.parseInt(command.toLowerCase().replace("wait ", ""));
			Tasks.wait(wait, () -> next(commands));
		} else
			Tasks.wait(3, () -> {
				runCommand(command);
				next(commands);
			});
	}

	private void next(List<String> input) {
		input.remove(0);
		run(input);
	}

}
