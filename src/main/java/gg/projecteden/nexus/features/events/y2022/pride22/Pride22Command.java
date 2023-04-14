package gg.projecteden.nexus.features.events.y2022.pride22;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Disabled
@NoArgsConstructor
@Aliases("pride")
public class Pride22Command extends IEventCommand {

	public Pride22Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return Pride22.get();
	}

	@NoLiterals
	void run() {
		send("Coming soon!");
	}

}
