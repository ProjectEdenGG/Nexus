package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aliases("nearby")
@Description("View nearby players and their distance from you. Players in this list can see your local chat.")
public class NearCommand extends CustomCommand {

	public NearCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg(value = "self", permission = "group.staff") Player player) {
		if (PlayerManager.get(player).isPlaying())
			error("This command cannot be used during Minigames");

		Set<Player> nearby = getNearbyPlayers(player, player, new TreeSet<>(Comparator.comparing(p -> location().distance(p.getLocation()))), false);
		nearby.remove(player);

		boolean showDistance = player.hasPermission("near.distance");

		String message = "&ePlayers nearby" + (isSelf(player) ? "" : " " + player.getName()) + "";
		if (nearby.size() == 0)
			send(message + ": &fNone");
		else
			send(new JsonBuilder(message + " (&3" + nearby.size() + "&e): &f").next(AdventureUtils.commaJoinText(nearby.stream()
				.map(_player -> {
					if (showDistance) {
						if (getDistance(player, _player) < Chat.getLocalRadius()) {
							return new JsonBuilder(Nickname.of(_player) + " (&3" + getDistance(player, _player) + "m&f)");
						} else {
							return new JsonBuilder(Nickname.of(_player) + " (&c" + getDistance(player, _player) + "m&f)")
								.hover(
									"&cChained by another player.\n" +
									"\n" +
									"&cThis player is not in your local\n" +
									"&cradius (" + Chat.getLocalRadius() + " blocks), but they're\n" +
									"&cincluded as they're in the local\n" +
									"&cradius of someone who is.\n"
								);
						}
					} else {
						return new JsonBuilder(Nickname.of(_player));
					}
				})
				.collect(Collectors.toList()))
			));
	}

	static public Set<Player> getNearbyPlayers(Player originalPlayer, Player checkPlayer, Set<Player> nearbyPlayers, boolean includeUnseen) {
		nearbyPlayers.add(checkPlayer);
		for (Player _player : new ArrayList<>(nearbyPlayers)) {
			UUID uuid = _player.getUniqueId();
			Stream<Player> stream = OnlinePlayers.where().world(checkPlayer.getWorld()).get().stream()
				.filter(_player2 -> !uuid.equals(_player2.getUniqueId())
					&& getDistance(_player, _player2) <= Chat.getLocalRadius());
			if (!includeUnseen)
				stream = stream.filter(_player2 -> PlayerUtils.canSee(originalPlayer, _player2));

			List<Player> nearby = stream.collect(Collectors.toList());

			for (Player _player2 : nearby) {
				if (!nearbyPlayers.contains(_player2))
					getNearbyPlayers(originalPlayer, _player2, nearbyPlayers, includeUnseen);
			}
		}
		return nearbyPlayers;
	}

	static private long getDistance(@Arg("self") Player player, Player _player) {
		return Math.round(player.getLocation().distance(_player.getLocation()));
	}

}
