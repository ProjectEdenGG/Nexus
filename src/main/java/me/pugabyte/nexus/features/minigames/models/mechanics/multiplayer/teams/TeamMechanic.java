package me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks;
import me.pugabyte.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.MultiplayerMechanic;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Time;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public abstract class TeamMechanic extends MultiplayerMechanic {
	private static final Set<String> TEAM_VOICE_CHANNELS = new HashSet<>();
	private static final BaseComponent[] RETURN_VC = new JsonBuilder().newline().next("&e&lClick here&f&3 to return to the Minigames voice channel.").command("voicechannel "+DiscordId.VoiceChannel.MINIGAMES.getId()).newline().build();

	static {
		TEAM_VOICE_CHANNELS.add(DiscordId.VoiceChannel.RED.getId());
		TEAM_VOICE_CHANNELS.add(DiscordId.VoiceChannel.BLUE.getId());
		TEAM_VOICE_CHANNELS.add(DiscordId.VoiceChannel.GREEN.getId());
		TEAM_VOICE_CHANNELS.add(DiscordId.VoiceChannel.YELLOW.getId());
		TEAM_VOICE_CHANNELS.add(DiscordId.VoiceChannel.WHITE.getId());
	}

	@Override
	public boolean isTeamGame() {
		return true;
	}

	public boolean usesTeamChannels() {
		return true;
	}

	public static Member getVoiceChannelMember(Player player) {
		Guild guild = Discord.getGuild();
		if (guild == null) return null;

		DiscordUser discordUser = new DiscordService().get(player);
		Member member = discordUser.getMember();
		if (member == null) {
			// user has no linked account, find a disc account with matching (nick)name
			Optional<Member> optionalMember = guild.getMembers().stream()
					.filter(fmember -> (fmember.getNickname() != null && fmember.getNickname().equalsIgnoreCase(player.getName()))
							|| fmember.getUser().getName().equalsIgnoreCase(player.getName()))
					.findAny();
			if (optionalMember.isPresent())
				member = optionalMember.get();
			else
				return null;
		}
		if (member.getVoiceState() == null || !member.getVoiceState().inVoiceChannel())
			return null;
		return member;
	}

	private PublicChannel getTeamChannel(Match match, Team team, boolean createChannel) {
		Map<Team, PublicChannel> teamChannels = match.getMatchData().getTeamChannels();
		if (teamChannels.containsKey(team))
			return teamChannels.get(team);
		else if (!createChannel)
			return null;

		ColorType colorType = team.getColorType();
		if (colorType == null)
			return null;

		String nickname;
		if (colorType.getName().startsWith("light "))
			nickname = colorType.getName().split(" ")[1];
		else
			nickname = colorType.getName();

		nickname = nickname.substring(0, 1);

		PublicChannel channel = PublicChannel.builder()
				.name(camelCase(colorType.getName()))
				.nickname(nickname)
				.color(colorType.getChatColor())
				.local(false)
				.crossWorld(true)
				.persistent(false)
				.build();

		teamChannels.put(team, channel);
		return channel;
	}

	private PublicChannel createTeamChannel(Match match, Team team) {
		return getTeamChannel(match, team, true);
	}

	private PublicChannel getTeamChannel(Minigamer minigamer, boolean createChannel) {
		Team team = minigamer.getTeam();
		ColorType colorType = team.getColorType();
		if (colorType == null)
			return null;
		Chatter chatter = new ChatService().get(minigamer.getPlayer());
		Optional<PublicChannel> optionalPublicChannel = chatter.getJoinedChannels().stream().filter(publicChannel -> publicChannel.getName().equalsIgnoreCase(colorType.getName())).findFirst();
		return optionalPublicChannel.orElseGet(() -> getTeamChannel(minigamer.getMatch(), team, createChannel));
	}

	private void joinTeamChannel(Team team, List<Minigamer> teamMembers) {
		if (!usesTeamChannels()) return;
		if (teamMembers.isEmpty()) return;

		Match match = teamMembers.get(0).getMatch();
		ChatColor chatColor = team.getColor();
		DiscordId.VoiceChannel vcID;

		if (chatColor == ChatColor.RED || chatColor == ChatColor.DARK_RED)
			vcID = DiscordId.VoiceChannel.RED;
		else if (chatColor == ChatColor.BLUE || chatColor == ChatColor.AQUA || chatColor == ChatColor.DARK_AQUA || chatColor == ChatColor.DARK_BLUE)
			vcID = DiscordId.VoiceChannel.BLUE;
		else if (chatColor == ChatColor.WHITE || chatColor == ChatColor.GRAY)
			vcID = DiscordId.VoiceChannel.WHITE;
		else if (chatColor == ChatColor.GREEN || chatColor == ChatColor.DARK_GREEN)
			vcID = DiscordId.VoiceChannel.GREEN;
		else if (chatColor == ChatColor.YELLOW || chatColor == ChatColor.GOLD)
			vcID = DiscordId.VoiceChannel.YELLOW;
		else
			vcID = null;

		PublicChannel teamChannel = createTeamChannel(match, team);

		JsonBuilder voiceMessageBuilder = new JsonBuilder();
		if (vcID != null) {
			voiceMessageBuilder.next("&e&lClick here&f&3 to join your team's voice channel").command("voicechannel " + vcID).newline();
			voiceMessageBuilder.initialize();
		}

		if (teamChannel == null && !voiceMessageBuilder.isInitialized()) return;

		BaseComponent[] message;
		if (voiceMessageBuilder.isInitialized())
			message = voiceMessageBuilder.build();
		else
			message = new BaseComponent[]{}; // not rly necessary but it makes IDE stop yelling that it's not initialized

		teamMembers.forEach(minigamer -> {
			// add user to text channel
			Chatter chatter = new ChatService().get(minigamer.getPlayer());
			if (teamChannel != null)
				chatter.setActiveChannel(teamChannel);

			// add voice channel text if present and if user is in voice
			if (voiceMessageBuilder.isInitialized()) {
				Member member = getVoiceChannelMember(minigamer.getPlayer());
				if (member != null) {
					// getVoiceChannelMember ensures these aren't null, but IDE is silly, so let's help it out
					assert member.getVoiceState() != null;
					assert member.getVoiceState().getChannel() != null;

					if (member.getVoiceState().getChannel().getId().equals(DiscordId.VoiceChannel.MINIGAMES.getId()))
						minigamer.getPlayer().sendMessage(message);
				}
			}
		});
	}

	private void joinTeamChannel(Minigamer minigamer) {
		joinTeamChannel(minigamer.getTeam(), Collections.singletonList(minigamer));
	}

	private void joinTeamChannel(Team team, Match match) {
		joinTeamChannel(team, team.getAliveMinigamers(match));
	}

	private void leaveTeamTextChannel(Minigamer minigamer) {
		if (!usesTeamChannels()) return;

		PublicChannel teamChannel = getTeamChannel(minigamer, false);
		if (teamChannel == null) return;
		Chatter chatter = new ChatService().get(minigamer.getPlayer());
		if (chatter.getActiveChannel() == teamChannel)
			chatter.setActiveChannel(Chat.StaticChannel.MINIGAMES.getChannel());
		chatter.leave(teamChannel);
	}

	private void leaveTeamVoiceChannel(Minigamer minigamer) {
		if (!usesTeamChannels()) return;

		Member member = getVoiceChannelMember(minigamer.getPlayer());
		if (member == null) return;
		// getVoiceChannelMember ensures these aren't null, but IDE is silly, so let's help it out
		assert member.getVoiceState() != null;
		assert member.getVoiceState().getChannel() != null;

		if (TEAM_VOICE_CHANNELS.contains(member.getVoiceState().getChannel().getId()))
			minigamer.getPlayer().sendMessage(RETURN_VC);
	}

	private void leaveTeamChannel(Minigamer minigamer) {
		leaveTeamTextChannel(minigamer);
		leaveTeamVoiceChannel(minigamer);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		match.getAliveTeams().forEach(team -> joinTeamChannel(team, match));
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		if (match.isStarted())
			joinTeamChannel(event.getMinigamer());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		event.getMatch().getMinigamers().forEach(this::leaveTeamChannel);
		super.onEnd(event);
	}

	/**
	 * Whether or not this Minigame uses auto-balancing (tries to balance the teams upon player death).
	 * Recommended to disable for gamemodes that manually set teams like Infection.
	 * @return whether to enable auto balancing
	 */
	public boolean usesAutoBalancing() {
		return true;
	}

	@Override
	public void announceWinners(Match match) {
		Arena arena = match.getArena();
		Map<ChatColor, Integer> scoreList = new HashMap<>();
		Map<Team, Integer> scores = match.getScores();

		int winningScore = getWinningScore(scores.values());
		List<Team> winners = getWinners(winningScore, scores);
		scores.keySet().forEach(team -> scoreList.put(team.getColor(), scores.get(team)));

		String announcement;
		if (winningScore == 0) {
			announcement = "No teams scored in &e" + arena.getDisplayName();
			Minigames.broadcast(announcement);
		} else {
			if (arena.getTeams().size() == winners.size()) {
				announcement = "All teams tied in &e" + arena.getDisplayName();
			} else {
				announcement = getWinnersString(winners) + "&e" + arena.getDisplayName();
			}
			Minigames.broadcast(announcement + getScoreList(scoreList));
		}
	}

	private String getWinnersString(List<Team> winners) {
		if (winners.size() > 1) {
			String result = winners.stream()
					.map(team -> team.getColoredName() + ChatColor.DARK_AQUA)
					.collect(Collectors.joining(", "));
			int lastCommaIndex = result.lastIndexOf(", ");
			if (lastCommaIndex >= 0) {
				result = new StringBuilder(result).replace(lastCommaIndex, lastCommaIndex + 2, " and ").toString();
			}
			return result + " tied in ";
		} else {
			return winners.get(0).getColoredName() + " &3won ";
		}
	}

	private List<Team> getWinners(int winningScore, Map<Team, Integer> scores) {
		List<Team> winners = new ArrayList<>();

		for (Team team : scores.keySet())
			if (scores.getOrDefault(team, 0).equals(winningScore))
				winners.add(team);

		return winners;
	}

	private String getScoreList(Map<ChatColor, Integer> scores) {
		StringBuilder scoreList = new StringBuilder(" &3( ");
		int counter = 0;
		for (ChatColor color : scores.keySet()) {
			scoreList.append(color)
					.append(scores.get(color).toString())
					.append(ChatColor.DARK_AQUA)
					.append(++counter != scores.size() ? " | " : " )");
		}
		return scoreList.toString();
	}

	Team getSmallestTeam(List<Minigamer> minigamers, List<Team> teams) {
		Map<Team, Integer> assignments = getCurrentAssignments(minigamers, teams);
		return getSmallestTeam(assignments);
	}

	private Team getSmallestTeam(Map<Team, Integer> assignments) {
		Team smallest = null;
		int min = Integer.MAX_VALUE;
		for (Map.Entry<Team, Integer> entry : assignments.entrySet()) {
			if (entry.getValue() < min) {
				smallest = entry.getKey();
				min = entry.getValue();
			}
		}

		return smallest;
	}

	private Map<Team, Integer> getCurrentAssignments(List<Minigamer> minigamers, List<Team> teams) {
		Map<Team, Integer> assignments = new HashMap<>();
		teams.forEach(team -> assignments.put(team, 0));
		minigamers.forEach(minigamer -> {
			if (minigamer.getTeam() != null)
				assignments.put(minigamer.getTeam(), assignments.get(minigamer.getTeam()) + 1);
		});
		return assignments;
	}

	@Override
	public boolean shouldBeOver(Match match) {
		Set<Team> teams = new HashSet<>();
		match.getMinigamers().stream().filter(Minigamer::isAlive).forEach(minigamer -> teams.add(minigamer.getTeam()));
		if (teams.size() == 1) {
			Nexus.log("Match has only one team left, ending");
			return true;
		}

		int winningScore = match.getArena().getCalculatedWinningScore(match);
		if (winningScore > 0)
			for (Team team : teams)
				if (team.getScore(match) >= winningScore) {
					match.getMatchData().setWinnerTeam(team);
					Nexus.log("Team match has reached calculated winning score (" + winningScore + "), ending");
					return true;
				}

		return false;
	}

	public void onTurnStart(Match match, Team team) {
		match.getMatchData().setTurnStarted(LocalDateTime.now());
	}

	public void onTurnEnd(Match match, Team team) {

	}

	public void nextTurn(Match match) {
		Arena arena = match.getArena();
		MatchData matchData = match.getMatchData();
		MatchTasks tasks = match.getTasks();

		if (match.isEnded() || matchData == null)
			return;

		if (matchData.getTurnTeam() != null) {
			onTurnEnd(match, matchData.getTurnTeam());
			matchData.setTurnTeam(null);
		}

		if (shouldBeOver(match)) {
			end(match);
			return;
		}

		if (matchData.getTurns() >= match.getArena().getMaxTurns()) {
			match.broadcast("Max turns reached, ending game");
			match.end();
			return;
		}

		if (matchData.getTurnTeamList().isEmpty()) {
			matchData.setTurnTeamList(new ArrayList<>(match.getAliveTeams()));
			if (shuffleTurnList())
				Collections.shuffle(matchData.getTurnTeamList());
		}

		tasks.cancel(MatchTaskType.TURN);

		Team team = matchData.getTurnTeamList().get(0);
		matchData.getTurnTeamList().remove(team);
		matchData.setTurnTeam(team);
		match.getScoreboard().update();

		onTurnStart(match, team);
		tasks.register(MatchTaskType.TURN, tasks.wait(arena.getTurnTime() * Time.SECOND.get(), () -> nextTurn(match)));
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		Match match = event.getMatch();
		Team team = event.getMinigamer().getTeam();
		if (team != null && team.equals(match.getMatchData().getTurnTeam()))
			if (team.getAliveMinigamers(match).size() == 0)
				nextTurn(match);

		leaveTeamChannel(event.getMinigamer());

		super.onQuit(event);
	}

	public boolean basicBalanceCheck(List<Minigamer> minigamers) {
		if (minigamers.isEmpty())
			return false;

		Match match = minigamers.get(0).getMatch();
		Arena arena = match.getArena();
		List<Team> teams = new ArrayList<>(arena.getTeams());

		int required = 0;
		for (Team team : teams) required += team.getMinPlayers();

		if (match.getMinigamers().size() < required) {
			error("Not enough players to meet team requirements!", match);
			return false;
		}

		return true;
	}

	protected List<BalanceWrapper> getBalanceWrappers(Minigamer minigamer) {
		return getBalanceWrappers(minigamer.getMatch());
	}

	protected List<BalanceWrapper> getBalanceWrappers(Match match) {
		// ALL PERCENTAGES HERE RANGE FROM 0 to 1 !!
		List<Team> teams = match.getArena().getTeams();
		List<BalanceWrapper> wrappers = new ArrayList<>();
		double percentageSum = 0; // sum of all balance percentages
		int noPercentage = 0; // count of teams w/o balance percentages
		for (Team team : teams) {
			BalanceWrapper wrapper = new BalanceWrapper(team, match);
			wrappers.add(wrapper);
			if (wrapper.getPercentage() != null)
				percentageSum += wrapper.getPercentage();
			else
				noPercentage++;
		}

		if (noPercentage > 0 && percentageSum < 1) {
			// evenly split the balance of teams that don't have a balance percentage (if there is any unassigned %)
			double percentage = (1d/noPercentage) * (1d-percentageSum);
			wrappers.stream().filter(wrapper -> wrapper.getPercentage() == null).forEach(wrapper -> wrapper.setPercentage(percentage));
		}

		// ensure percentages add up to 100
		double totalPercentage = wrappers.stream().mapToDouble(BalanceWrapper::getPercentage).sum();
		wrappers.forEach(wrapper -> wrapper.setPercentage(wrapper.getPercentage() / totalPercentage));

		return wrappers;
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		// auto-balancing
		Match match = event.getMatch();
		Minigamer minigamer = event.getMinigamer();
		super.onDeath(event);
		if (!usesAutoBalancing())
			return;
		if (!minigamer.isAlive() || match.isEnded())
			return;
		if (minigamer.getTeam().getMinigamers(match).size()-1 < minigamer.getTeam().getMinPlayers())
			return;

		List<BalanceWrapper> wrappers = getBalanceWrappers(match).stream()
				.filter(wrapper -> !wrapper.getTeam().equals(minigamer.getTeam()) && // only try to auto-balance to other teams
						wrapper.percentageDiscrepancy() > 0 &&
						wrapper.getNeededPlayers() != -1 &&
						wrapper.extraPlayerPercentDiscrepancy() >= 0).collect(Collectors.toList());
		if (wrappers.isEmpty())
			return;
		// sort teams by closest to being equal (inverse of natural sort)
		wrappers.sort(Collections.reverseOrder());
		// select randomly if multiple teams are equal
		List<BalanceWrapper> randomWrappers = new ArrayList<>();
		randomWrappers.add(wrappers.get(0));
		double val = wrappers.get(0).extraPlayerPercentDiscrepancy();
		int index = 1; // iterator var
		while (index < wrappers.size() && Math.abs(wrappers.get(index).extraPlayerPercentDiscrepancy() - val) < 0.0001d) {
			randomWrappers.add(wrappers.get(index));
			index++;
		}
		// assign team
		leaveTeamChannel(minigamer);
		Team team = RandomUtils.randomElement(randomWrappers).getTeam();
		minigamer.setTeam(team);
		minigamer.tell("", false);
		minigamer.tell("&3You have been auto balanced to "+team.getColoredName());
		joinTeamChannel(minigamer);
	}

	@Override
	public void balance(List<Minigamer> minigamers) {
		minigamers = new ArrayList<>(minigamers); // cries in pass by reference
		if (!basicBalanceCheck(minigamers))
			return;

		minigamers.forEach(minigamer -> minigamer.setTeam(null)); // clear teams
		Collections.shuffle(minigamers); // lets us assign teams to players in random order
		Match match = minigamers.get(0).getMatch();
		List<Team> teams = match.getArena().getTeams(); // old code made a new list so im doing it too

		// only one team, no need to bother with math
		if (teams.size() == 1) {
			minigamers.forEach(minigamer -> minigamer.setTeam(teams.get(0)));
			return;
		}

		// create wrapper objects
		List<BalanceWrapper> wrappers = getBalanceWrappers(match);

		// add players to teams that need them (i.e. have a minimum player count that is not satisfied)
		while (!minigamers.isEmpty()) {
			Optional<BalanceWrapper> needsPlayers = wrappers.stream().filter(wrapper -> wrapper.getNeededPlayers() > 0).findFirst();
			if (!needsPlayers.isPresent())
				break;
			Team team = needsPlayers.get().getTeam();
			minigamers.remove(0).setTeam(team);
		}

		// add rest of players according to percentages
		while (!minigamers.isEmpty()) {
			// this basically finds the team with the largest percent
			wrappers = wrappers.stream().filter(wrapper -> wrapper.getNeededPlayers() != -1).sorted().collect(Collectors.toList());
			if (wrappers.isEmpty())
				break;
			// get teams with matching percentage discrepancies (ie the teams are perfectly balanced) and randomly
			//  select one of them
			List<BalanceWrapper> equalWrappers = new ArrayList<>();
			equalWrappers.add(wrappers.get(0));
			int index = 1;
			double val = wrappers.get(0).percentageDiscrepancy();
			while (index < wrappers.size() && Math.abs(wrappers.get(index).percentageDiscrepancy() - val) < 0.0001d) {
				equalWrappers.add(wrappers.get(index));
				index++;
			}
			Team team = RandomUtils.randomElement(equalWrappers).getTeam();
			minigamers.remove(0).setTeam(team);
		}

		// leftover players means the teams all (somehow) reached their max player count
		minigamers.forEach(minigamer -> {
			minigamer.tell("Could not assign you to a team!");
			minigamer.quit();
		});
	}

	@Data
	@EqualsAndHashCode
	public static class BalanceWrapper implements Comparable<BalanceWrapper> {
		private final Team team;
		private final Match match;
		private Double percentage;
		private BalanceWrapper(Team team, Match match) {
			this.team = team;
			this.match = match;
			if (team.getBalancePercentage() == -1)
				percentage = null;
			else
				percentage = team.getBalancePercentage()/100d;
		}

		/**
		 * How many players this team needs to reach its minimum player quota.
		 * Returns -1 if the team has hit is maximum player count.
		 * @return integer number of players
		 */
		public int getNeededPlayers() {
			int teamPlayers = team.getMinigamers(match).size();
			if (team.getMaxPlayers() > -1 && teamPlayers >= team.getMaxPlayers())
				return -1;
			return Math.max(0, team.getMinPlayers()-teamPlayers);
		}

		public int getTeamPlayers() {
			return team.getMinigamers(match).size();
		}

		public int getTotalPlayers() {
			// should this ignore dead players (spectators)? i'm not sure... i can't think of a minigame that would be
			//  affected by that either way
			return (int) match.getMinigamers().stream().filter(minigamer -> minigamer.getTeam() != null).count();
		}

		/**
		 * Calculates the difference between the team's specified percentage and its current percentage (i.e. the
		 * current balance of the match). A negative score is unbalanced in favor of the team, a positive score is
		 * unbalanced in favor of other teams. Larger scores mean more unbalanced.
		 * @return a score ranging from -1 to 1
		 */
		public double percentageDiscrepancy() {
			return percentageDiscrepancy(getTeamPlayers(), getTotalPlayers());
		}

		/**
		 * Manually calculate the percentage discrepancy of a team
		 * @param teamPlayers players on this team
		 * @param totalPlayers all current players in the minigame
		 * @return a score ranging from -1 to 1
		 */
		public double percentageDiscrepancy(int teamPlayers, int totalPlayers) {
			double matchPercentage;
			if (totalPlayers == 0)
				matchPercentage = 0; // this is the first added player, assume all teams are on 0%
			else
				matchPercentage = (double)teamPlayers/totalPlayers; // get % of players on this team
			return percentage-matchPercentage;
		}

		public double extraPlayerPercentDiscrepancy() {
			return percentageDiscrepancy(getTeamPlayers()+1, getTotalPlayers());
		}

		/**
		 * Compares which of two teams has a larger player discrepancy. A negative value means this team has a larger
		 * discrepancy (to allow for naturally sort in descending order)
		 * @param otherWrapper the other team
		 * @return a score ranging from -1 to 1
		 */
		@Override
		public int compareTo(@NotNull BalanceWrapper otherWrapper) {
			return (int) ((otherWrapper.percentageDiscrepancy()-percentageDiscrepancy())*100);
		}
	}
}
