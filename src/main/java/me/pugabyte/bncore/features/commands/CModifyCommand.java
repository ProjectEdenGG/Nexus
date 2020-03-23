package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("lwc")
public class CModifyCommand extends CustomCommand {

	public CModifyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if ("or".equalsIgnoreCase(arg(2))) {
			args().remove(1);
			fallback();
		}
		for (int i = 0; i < args().size(); i++) {
			if ("add".equalsIgnoreCase(args().get(i))) {
				args().remove(i);
			}
		}
		fallback();
	}

}
