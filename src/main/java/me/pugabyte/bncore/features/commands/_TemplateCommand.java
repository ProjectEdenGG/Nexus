package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

// Copy and rename this file as a template for a new command

@Permission("permission")
public class _TemplateCommand extends CustomCommand {

	public _TemplateCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void help() {
	}

	@Path("run")
	void run() {
	}
}
