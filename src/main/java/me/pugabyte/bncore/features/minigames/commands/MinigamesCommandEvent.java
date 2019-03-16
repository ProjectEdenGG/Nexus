package me.pugabyte.bncore.features.minigames.commands;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;

@Data
public class MinigamesCommandEvent {
	@Getter
	@Setter
	@NonNull
	public CommandSender sender;
	@Getter
	@Setter
	@NonNull
	public String[] args;

	public void reply(String message) {
		sender.sendMessage(PREFIX + message);
	}

}
