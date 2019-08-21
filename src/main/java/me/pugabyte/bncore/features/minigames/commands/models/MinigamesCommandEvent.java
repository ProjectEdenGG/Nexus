package me.pugabyte.bncore.features.minigames.commands.models;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;

@Data
public class MinigamesCommandEvent {
	@NonNull
	public CommandSender sender;
	@NonNull
	public String[] args;

	public void reply(String message) {
		sender.sendMessage(PREFIX + message);
	}

}
