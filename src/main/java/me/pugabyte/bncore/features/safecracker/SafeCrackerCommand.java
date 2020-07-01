package me.pugabyte.bncore.features.safecracker;

import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.safecracker.menus.SafeCrackerInventories;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayerService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SafeCrackerCommand extends CustomCommand {


	public SafeCrackerEventService eventService = new SafeCrackerEventService();
	public SafeCrackerEvent event = eventService.get();
	public SafeCrackerEvent.SafeCrackerGame game = eventService.getActiveEvent();
	public SafeCrackerPlayerService playerService = new SafeCrackerPlayerService();
	public SafeCrackerPlayer safeCrackerPlayer = playerService.get(player());

	public SafeCrackerCommand(CommandEvent event) {
		super(event);
	}

	@Path("check")
	void check() {
		if (safeCrackerPlayer.getGames().get(game.getName()).getStarted() == null)
			error("You have not started the current SafeCracker game");
		SafeCrackerInventories.openCheckMenu(player());
	}

	@Path("answer <answer...>")
	void answer(String answer) {
		if (SafeCracker.playerClickedNPC.keySet().contains(player().getPlayer())) {
			safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(SafeCracker.playerClickedNPC.get(player())).setAnswer(answer);
			if (game.getNpcs().get(SafeCracker.playerClickedNPC.get(player().getPlayer())).getAnswers().contains(answer.toLowerCase())) {
				send("&3" + SafeCracker.playerClickedNPC.get(player().getPlayer()) + " >&e " + Utils.getRandomElement(SafeCracker.correctResponses));
				safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(SafeCracker.playerClickedNPC.get(player())).setCorrect(true);
			} else {
				send("&3" + SafeCracker.playerClickedNPC.get(player().getPlayer()) + " >&c " + Utils.getRandomElement(SafeCracker.wrongResponses));
				safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(SafeCracker.playerClickedNPC.get(player())).setCorrect(false);
			}
			playerService.save(safeCrackerPlayer);
		} else error("You must find an NPC before answering");
	}

	@Path("solve <answer...>")
	void solve(String answer) {
		if (safeCrackerPlayer.getGames().get(game.getName()).getScore() != 0)
			error("You have already correctly solved the riddle");
		if (eventService.getActiveEvent().getAnswer().equalsIgnoreCase(answer)) {
			int score = (int) Math.abs(Duration.between(LocalDateTime.now(), safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted()).getSeconds() - 1);
			safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).setScore(score);
			playerService.save(safeCrackerPlayer);
			send(PREFIX + "You correctly solved the riddle. You finished with a score of &e" + score);
			Discord.staffLog("```[SafeCracker] " + player().getName() + " - " + score + "```");
		} else send(PREFIX + "&c" + Utils.getRandomElement(SafeCracker.wrongResponses));
	}

	@Path("start")
	void start() {
		if (safeCrackerPlayer.getGames() == null)
			safeCrackerPlayer.setGames(new HashMap<>());
		if (safeCrackerPlayer.getGames().containsKey(game.getName()))
			error("You have already started the SafeCracker game");
		safeCrackerPlayer.getGames().put(game.getName(), new SafeCrackerPlayer.Game());
		safeCrackerPlayer.getGames().get(game.getName()).setStarted(LocalDateTime.now());
		playerService.save(safeCrackerPlayer);
		send(PREFIX + "You just started the SafeCracker game");
		send(PREFIX + "Solve: &e" + game.getRiddle());
		send(PREFIX + "Use &c/safecracker solve &3to solve the riddle");
	}

	@Path("admin edit")
	@Permission("group.staff")
	void edit() {
		SafeCrackerInventories.openAdminMenu(player());
	}

	@Path("admin reset [player]")
	@Permission("group.staff")
	void reset(@Arg("self") OfflinePlayer player) {
		SafeCrackerPlayer safeCrackerPlayer = playerService.get(player);
		safeCrackerPlayer.setGames(new HashMap<>());
		playerService.save(safeCrackerPlayer);
		send(PREFIX + "Successfully reset &e" + player.getName());
	}

	@Path("scores")
	@Permission("group.staff")
	void scores() {
		send(PREFIX + "Scores for current event:");
		LinkedHashMap<Player, Integer> scores = playerService.getScores(eventService.getActiveEvent());
		int i = 1;
		for (Player player : scores.keySet()) {
			send("&e" + i++ + ". " + player.getName() + ": &3" + scores.get(player));
		}
	}

}
