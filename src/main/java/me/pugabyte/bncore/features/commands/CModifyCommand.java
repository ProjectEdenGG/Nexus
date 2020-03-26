package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Fallback("lwc")
public class CModifyCommand extends CustomCommand {

	public CModifyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		List<String> filter = Arrays.asList("add", "or");

		List<String> toRemove = new ArrayList<>();
		args().stream().filter(arg -> filter.contains(arg.toLowerCase())).forEach(toRemove::add);
		args().removeAll(toRemove);

		fallback();
	}

}
