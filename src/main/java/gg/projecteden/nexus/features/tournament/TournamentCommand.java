package gg.projecteden.nexus.features.tournament;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.tournament.models.Tournament;
import gg.projecteden.nexus.features.tournament.models.Tournament.Match;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import org.bukkit.OfflinePlayer;

@Aliases("tourny")
@Permission(Group.SENIOR_STAFF)
@Environments(Env.UPDATE)
public class TournamentCommand extends CustomCommand {

	private static Tournament tournament = null;

	public TournamentCommand(CommandEvent event) {
		super(event);
	}

	@Path("create <id>")
	void create(String id) {
		tournament = new Tournament(id, location());
		send("&3Created new Tournament: &e" + tournament.getId());
	}

	@Path("add <player>")
	void addPlayer(OfflinePlayer player) {
		checkStarted();
		checkFinished();

		if (tournament.getPlayerUUIDs().contains(player.getUniqueId()))
			error("This player is already in the tournament");

		tournament.addPlayer(player);
		send("&3Added &e" + Nickname.of(player) + " &3to tournament: &e" + tournament.getId());
	}

	@Path("remove <player>")
	void removePlayer(OfflinePlayer player) {
		checkStarted();
		checkFinished();

		if (!tournament.getPlayerUUIDs().contains(player.getUniqueId()))
			error("This player is not in the tournament");

		tournament.removePlayer(player);
		send("&3Removed &e" + Nickname.of(player) + " &3from tournament: &e" + tournament.getId());
	}

	@Path("start")
	void start() {
		checkStarted();
		checkFinished();

		tournament.start();
		send("&3Started tournament: &e" + tournament.getId());
	}

	@Path("getNextMatch")
	void getNextMatch() {
		checkNotStarted();
		checkFinished();

		Match match = tournament.getNextMatch();
		send("&3Next match: " + match.getDisplay());
	}

	@Path("setMatchWinner <player>")
	void setMatchWinner(OfflinePlayer player) {
		checkNotStarted();
		checkFinished();

		tournament.playNextMatch(player);
		send("&3Set match winner to &e" + Nickname.of(player));

		if (tournament.isFinished()) {
			send("&3Tournament champion: &e" + Nickname.of(tournament.getChampionUUID()));
			return;
		}
	}

	@Path("delete")
	void delete() {
		send("&3Deleted Tournament: &e" + tournament.getId());
		tournament.delete();
		tournament = null;
	}

	@Path("display")
	void display() {
		if (tournament == null)
			error("Tournament not created");

		tournament.printBracket(player());
	}

	//

	private void checkNotStarted() {
		if (!tournament.isStarted())
			error("Tournament not started");
	}

	private void checkStarted() {
		if (tournament.isStarted())
			error("Tournament already started");
	}

	private void checkFinished() {
		if (tournament.isFinished())
			error("Tournament has finished");
	}
}
