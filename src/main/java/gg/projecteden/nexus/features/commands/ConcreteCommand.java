package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import org.bukkit.event.Listener;

@NoArgsConstructor
public class ConcreteCommand extends CustomCommand implements Listener {

	public ConcreteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void concrete() {
		error("The concrete menu has been replaced with a recipe, see &c/customrecipes");
	}

}
