package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.JsonBuilder;

public class JsonBuilderCommand extends CustomCommand {

	public JsonBuilderCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("test1")
	void test1() {
		new JsonBuilder()
				.next("plain text no events ")
				.group()
				.next("// command ")
				.command("/echo hi")
				.group()
				.next("// hover and command 2 ")
				.hover("hover")
				.command("/echo hi2")
				.send(sender());
	}

}
