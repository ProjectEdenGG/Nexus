package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.nameplates.protocol.FakeEntityManager;
import gg.projecteden.nexus.features.nameplates.protocol.ProtocolManager;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

@Getter
public class Nameplates extends Feature {
	private final ProtocolManager protocolManager;
	private final FakeEntityManager fakeEntityManager;
	private final Team team;
	private final String teamName = "NP_HIDE";
	private final boolean manageTeams = true;

	public Nameplates() {
		System.out.println("===== Nameplates()");
		System.out.println("ProtocolManager v");
		protocolManager = new ProtocolManager();
		System.out.println("ProtocolManager ^");
		System.out.println("FakeEntityManager v");
		fakeEntityManager = new FakeEntityManager();
		System.out.println("FakeEntityManager ^");

		System.out.println("NameplatesListener v");
		new NameplatesListener();
		System.out.println("NameplatesListener ^");

		final Scoreboard scoreboard = Nexus.getInstance().getServer().getScoreboardManager().getMainScoreboard();

		Team team;

		if (manageTeams) {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();

			team = scoreboard.registerNewTeam(teamName);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		} else {
			team = scoreboard.getTeam(teamName);
			if (team != null)
				team.unregister();
		}

		this.team = team;
	}

	@Override
	public void onStart() {
		Tasks.wait(1, this.fakeEntityManager::onStart);
	}

	@Override
	public void onStop() {
		this.protocolManager.shutdown();
		this.fakeEntityManager.shutdown();
	}

	public static Nameplates get() {
		return Features.get(Nameplates.class);
	}

	public static String of(Player player, Player viewer) {
		final Presence presence = Presence.of(player);
		final Minigamer minigamer = PlayerManager.get(player);

		final JsonBuilder nameplate = new JsonBuilder();
		if (minigamer.isPlaying())
			nameplate.next(minigamer.getColoredName());
		else
			nameplate.next(presence.ingame()).next(" ").next(Nerd.of(player).getChatFormat(new ChatterService().get(viewer)));

		return nameplate.serialize();
	}

}
