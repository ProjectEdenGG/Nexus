package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Permission("group.admin")
public class ScoreboardTeamUtilsCommand extends CustomCommand {

	public ScoreboardTeamUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("teamsOf <player> [page]")
	void teamsOf(OfflinePlayer player, @Arg("1") int page) {
		List<Team> teams = teams().stream()
			.filter(team -> team.getEntries().contains(player.getName()) || team.getEntries().contains(player.getUniqueId().toString()))
			.collect(Collectors.toList());

		if (teams.isEmpty())
			error(Nickname.of(player) + " doesn't belong to any teams");

		send(PREFIX + "Teams of " + player.getName());
		paginate(teams, (team, index) -> json("&3" + index + " &e" + team.getName()), "teamdebug teamsOf " + player.getName(), page);
	}

	@Path("cleanup")
	void cleanup() {
		int unregistered = 0;
		for (Team team : teams())
			if (team.getEntries().isEmpty()) {
				send("&7Unregistering " + team.getName());
				team.unregister();
				++unregistered;
			}

		send(PREFIX + "Unregistered " + unregistered + " teams");
	}

	@Path("count")
	void count() {
		int empty = 0;
		int used = 0;
		for (Team team : teams())
			if (team.getEntries().isEmpty())
				++empty;
			else
				++used;

		send(PREFIX + "Found &e" + used + " non empty &3teams and &e" + empty + " empty &3teams");
	}

	@Path("list [page] [--empty]")
	void list(
		@Arg("1") int page,
		@Switch Boolean empty
	) {
		List<Team> teams = new ArrayList<>();
		for (Team team : teams()) {
			if (empty != null && team.getEntries().isEmpty() != empty)
				continue;

			teams.add(team);
		}

		if (teams.isEmpty())
			error("No teams found");

		final String command = "teamdebug list"
			+ (empty == null ? "" : " --empty=" + empty);

		send(PREFIX + "Found " + teams.size() + " teams");
		paginate(teams, (team, index) -> json("&3" + index + " &e" + team.getName()), command, page);
	}

	@NotNull
	private Set<Team> teams() {
		return Bukkit.getScoreboardManager().getMainScoreboard().getTeams();
	}

}
