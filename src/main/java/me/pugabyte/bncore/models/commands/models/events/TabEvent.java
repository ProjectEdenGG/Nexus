package me.pugabyte.bncore.models.commands.models.events;

import lombok.NonNull;
import me.pugabyte.bncore.models.commands.models.CustomCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TabEvent extends CommandEvent {

	public TabEvent(@NonNull CommandSender sender, @NonNull CustomCommand command, @NonNull List<String> args) {
		super(sender, command, args);
	}

	@Override
	public void handleException(Exception ex) {
		ex.printStackTrace();
	}

}
