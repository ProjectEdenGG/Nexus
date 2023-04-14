package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.DescriptionExtra;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.PlayerUtils.uuidsOf;

@Aliases("nearby")
public class NearCommand extends CustomCommand {

	public NearCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("View nearby players and their distance from you")
	@DescriptionExtra("Players in this list can see your local chat")
	void run(@Optional("self") @Permission(Group.STAFF) Player player) {
		if (Minigamer.of(player).isPlaying())
			error("This command cannot be used during Minigames");

		Set<Player> nearby = new Near(player).sorted().find();
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
									"&cChained by another player.",
									"",
									"&cThis player is not in your local",
									"&cradius (" + Chat.getLocalRadius() + " blocks), but they're",
									"&cincluded as they're in the local",
									"&cradius of someone who is."
								);
						}
					} else {
						return new JsonBuilder(Nickname.of(_player));
					}
				})
				.collect(Collectors.toList()))
			));
	}

	public static class Near {
		private final Player origin;
		private Set<Player> results;
		private boolean includeUnseen;
		private Player current;

		public Near(Player origin) {
			this.origin = origin;
			this.current = origin;
			this.results = new HashSet<>();
		}

		public Near includeUnseen() {
			this.includeUnseen = true;
			return this;
		}

		public Near sorted() {
			this.results = new TreeSet<>(Comparator.comparing(player -> distance(origin, player).get()));
			return this;
		}

		public Set<Player> find() {
			results.add(current);

			for (Player player : new ArrayList<>(results))
				OnlinePlayers.where()
					.radius(player.getLocation(), Chat.getLocalRadius())
					.exclude(uuidsOf(results))
					.filter(chained -> includeUnseen || PlayerUtils.canSee(origin, chained))
					.get()
					.forEach(chained -> {
						current = chained;
						find();
					});

			return results;
		}

	}

	private static long getDistance(Player from, Player to) {
		return Math.round(distance(from, to).getRealDistance());
	}

}
