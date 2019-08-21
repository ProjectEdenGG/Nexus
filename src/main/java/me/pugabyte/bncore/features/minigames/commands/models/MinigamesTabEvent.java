package me.pugabyte.bncore.features.minigames.commands.models;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

@Data
public class MinigamesTabEvent {
	@NonNull
	public CommandSender sender;
	@NonNull
	public String[] args;

}
