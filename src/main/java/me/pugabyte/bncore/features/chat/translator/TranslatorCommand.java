package me.pugabyte.bncore.features.chat.translator;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
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
			if (translators != null && translators.contains(player().getUniqueId())) {
				Translator.getMap().get(player.getUniqueId()).remove(player().getUniqueId());
				send(PREFIX + "You are no longer translating " + player.getDisplayName());
			} else {
				send(PREFIX + "You are not translating that player");
			}
			return;
		}

		for (UUID uuid : Translator.getMap().keySet())
			Translator.getMap().get(uuid).remove(player().getUniqueId());

		send(PREFIX + "Stopping all active translations.");
	}

	@Path("<player>")
	@Permission("use")
	void translate(Player player) {
		if (player() == player)
			throw new InvalidInputException("You cannot translate yourself");

		ArrayList<UUID> uuids = new ArrayList<UUID>() {{
			add(player().getUniqueId());
			if (Translator.getMap().containsKey(player.getUniqueId()))
				addAll(Translator.getMap().get(player.getUniqueId()));
		}};
		Translator.getMap().put(player.getUniqueId(), uuids);

		send(PREFIX + "You are now translating messages from " + player.getDisplayName() + ".");
	}

}
