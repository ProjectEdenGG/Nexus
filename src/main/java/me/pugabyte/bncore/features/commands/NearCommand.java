package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("nearby")
public class NearCommand extends CustomCommand {

	public NearCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		List<Player> nearby = Bukkit.getOnlinePlayers().stream()
				.filter(_player -> player.getUniqueId() != _player.getUniqueId())
				.filter(_player -> player.getWorld() == _player.getWorld())
				.filter(_player -> getDistance(player, _player) <= Chat.getLocalRadius())
				.filter(_player -> Utils.canSee(player(), _player))
				.collect(Collectors.toList());

		boolean showDistance = player.hasPermission("near.distance");

		String message = "&ePlayers nearby" + (isSelf(player) ? "" : " " + player.getName()) + ": &f";
		if (nearby.size() == 1)
			send(message + "None");
		else
			send(message + nearby.stream()
					.map(_player -> {
						if (showDistance)
							return _player.getName() + " (&3" + getDistance(player, _player) + "m&f)";
						else
							return _player.getName();
					})
					.collect(Collectors.joining(", ")));
	}

	private long getDistance(@Arg("self") Player player, Player _player) {
		return Math.round(player.getLocation().distance(_player.getLocation()));
	}

}
