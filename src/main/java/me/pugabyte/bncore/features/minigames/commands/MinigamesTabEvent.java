package me.pugabyte.bncore.features.minigames.commands;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.command.CommandSender;

@Data
public class MinigamesTabEvent {
	@Getter
	@Setter
	@NonNull
	public CommandSender sender;
	@Getter
	@Setter
	@NonNull
	public String[] args;

}
