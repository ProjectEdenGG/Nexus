package me.pugabyte.nexus.features.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class EventUserCommand extends CustomCommand {

	public EventUserCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("debug")
	void debug() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		gson.fromJson()
	}

}
