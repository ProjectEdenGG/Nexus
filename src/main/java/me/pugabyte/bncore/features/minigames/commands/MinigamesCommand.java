package me.pugabyte.bncore.features.minigames.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.NoPermissionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;

public abstract class MinigamesCommand {
	@Getter
	@Setter
	@NonNull
	protected String name, permission;
	@Getter
	@Setter
	protected String[] args;
	@Getter
	@Setter
	protected Minigamer minigamer;
	@Getter
	@Setter
	@NonNull
	protected boolean playerOnly = true;

	protected abstract void execute(MinigamesCommandEvent event) throws InvalidInputException;

	public final void run(MinigamesCommandEvent event) {
		args = event.getArgs();

		try {
			CommandSender sender = event.getSender();
			if (playerOnly) {
				if (!(sender instanceof Player))
					throw new InvalidInputException("You must be in-game to use this command!");

				if (!sender.hasPermission("minigames." + permission))
					throw new NoPermissionException();

				minigamer = PlayerManager.get((Player) sender);
			}

			execute(event);
		} catch (InvalidInputException | NoPermissionException ex) {
			event.reply(PREFIX + ex.getMessage());
		}
	}

	protected List<String> tab(MinigamesTabEvent event) {
		return null;
	}

	public final List<String> run(MinigamesTabEvent event) {
		args = event.getArgs();
		CommandSender sender = event.getSender();

		if (!sender.hasPermission("minigames." + permission))
			return null;

		return tab(event);
	}


}
