package gg.projecteden.nexus.features.events.y2022.halloween22;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Aliases("halloween")
public class Halloween22Command extends IEventCommand {

	public Halloween22Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return Halloween22.get();
	}

	@Path
	void run() {
		send("Coming soon!");
	}

}
