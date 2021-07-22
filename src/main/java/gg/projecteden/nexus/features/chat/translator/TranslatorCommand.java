package gg.projecteden.nexus.features.chat.translator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

@Aliases("translate")
@Permission("translate")
public class TranslatorCommand extends CustomCommand {
	public TranslatorCommand(CommandEvent event) {
		super(event);
	}

	@Path("stop [player]")
	@Permission("use")
	void remove(Player player) {
		if (player != null) {
			ArrayList<UUID> translators = Translator.getMap().get(player.getUniqueId());
			if (translators != null && translators.contains(uuid())) {
				Translator.getMap().get(player.getUniqueId()).remove(uuid());
				send(PREFIX + "You are no longer translating " + player.getDisplayName());
			} else {
				send(PREFIX + "You are not translating that player");
			}
			return;
		}

		for (UUID uuid : Translator.getMap().keySet())
			Translator.getMap().get(uuid).remove(uuid());

		send(PREFIX + "Stopping all active translations.");
	}

	@Path("<player>")
	@Permission("use")
	void translate(Player player) {
		if (player() == player)
			throw new InvalidInputException("You cannot translate yourself");

		ArrayList<UUID> uuids = new ArrayList<>() {{
			add(uuid());
			if (Translator.getMap().containsKey(player.getUniqueId()))
				addAll(Translator.getMap().get(player.getUniqueId()));
		}};
		Translator.getMap().put(player.getUniqueId(), uuids);

		send(PREFIX + "You are now translating messages from " + player.getDisplayName() + ".");
	}

}
