package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"weburl", "web", "weblink"})
public class WebsiteCommand extends CustomCommand {

	public WebsiteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void website() {
		send("&5https://bnn.gg");
	}

}
