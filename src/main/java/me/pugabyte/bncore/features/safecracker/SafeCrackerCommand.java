package me.pugabyte.bncore.features.safecracker;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.safecracker.menus.SafeCrackerInventories;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer.Game;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer.SafeCrackerPlayerNPC;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayerService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils.RandomUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

@NoArgsConstructor
public class SafeCrackerCommand extends CustomCommand implements Listener {

	public SafeCrackerEventService eventService = new SafeCrackerEventService();
	public SafeCrackerEvent event = eventService.get();
	public SafeCrackerEvent.SafeCrackerGame game = eventService.getActiveEvent();
	public SafeCrackerPlayerService playerService = new SafeCrackerPlayerService();
	public SafeCrackerPlayer safeCrackerPlayer;

	public SafeCrackerCommand(CommandEvent event) {
		super(event);
		safeCrackerPlayer = playerService.get(player());
	}

	@Path("check")
	void check() {
		if (safeCrackerPlayer.getGames().get(game.getName()).getStarted() == null)
			error("You have not started the current SafeCracker game");
		SafeCrackerInventories.openCheckMenu(player());
	}

	@Path("answer <answer...>")
	void answer(String answer) {
		if (SafeCracker.playerClickedNPC.containsKey(player())) {
			SafeCrackerPlayerNPC npc = safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(SafeCracker.playerClickedNPC.get(player()));
			npc.setAnswer(answer);

			boolean correct = answerIsCorrect(answer);
			npc.setCorrect(correct);

			if (correct) {
				player().playSound(player().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
				send("&3" + SafeCracker.playerClickedNPC.get(player()) + " >&e " + RandomUtils.randomElement(SafeCracker.correctResponses));
			} else {
				player().playSound(player().getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 2F);
				send("&3" + SafeCracker.playerClickedNPC.get(player()) + " >&c " + RandomUtils.randomElement(SafeCracker.wrongResponses));
			}

			playerService.save(safeCrackerPlayer);
		} else error("You must find an NPC before answering");
	}

	public boolean answerIsCorrect(String answer) {
		for (String _answer : game.getNpcs().get(SafeCracker.playerClickedNPC.get(player())).getAnswers())
			if (answer.equalsIgnoreCase(_answer))
				return true;
		return false;
	}

//	@Path("solve <answer...>")
//	void solve(String answer) {
//		if (safeCrackerPlayer.getGames().get(game.getName()).getScore() != 0)
//			error("You have already correctly solved the riddle");
//		if (safeCrackerPlayer.getGames().get(game.getName()).getNpcs().size() != game.getNpcs().size())
//			error("You have not found all the NPCs to solve the riddle. Keep hunting!");
//		if (eventService.getActiveEvent().getAnswer().equalsIgnoreCase(answer)) {
//			int score = (int) Math.abs(Duration.between(LocalDateTime.now(), safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted()).getSeconds() - 1);
//			safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).setScore(score);
//			playerService.save(safeCrackerPlayer);
//			send(PREFIX + "You correctly solved the riddle. You finished with a score of &e" + score);
//			Discord.staffLog("```[SafeCracker] " + player().getName() + " - " + score + "```");
//		} else send(PREFIX + "&c" + Utils.getRandomElement(SafeCracker.wrongResponses));
//	}

	@Path("start")
	void start() {
		if (safeCrackerPlayer.getGames() == null)
			safeCrackerPlayer.setGames(new HashMap<>());

		if (!safeCrackerPlayer.getGames().containsKey(game.getName())) {
			Game game = new Game();
			game.setStarted(LocalDateTime.now());
			safeCrackerPlayer.getGames().put(this.game.getName(), game);
			playerService.save(safeCrackerPlayer);
			send(PREFIX + "You just started the SafeCracker event");
		} else if (safeCrackerPlayer.getGames().get(game.getName()).isFinished())
			error("You have already correctly solved the riddle and finished the game");

		send(PREFIX + "The riddle you are trying to solve: &e" + game.getRiddle());
		send("&3You can use &e/safecracker check &3to check your progress");
		send("&3When you have found all the NPCs, click the safe to solve the riddle");
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

	@Path("admin riddle <riddle...>")
	@Permission("group.staff")
	void riddle(String riddle) {
		game.setRiddle(riddle);
		eventService.save(event);
		send(PREFIX + "Set the current riddle to: &e" + riddle);
		SafeCrackerInventories.openAdminMenu(player());
	}

	@Path("question <question...>")
	@Permission("group.staff")
	void question(String question) {
		if (!SafeCracker.adminQuestionMap.containsKey(player()))
			error("You must select an NPC in the GUI first");
		game.getNpcs().get(SafeCracker.adminQuestionMap.get(player())).setQuestion(question);
		eventService.save(event);
		send(PREFIX + "Set &e" + SafeCracker.adminQuestionMap.get(player()) + "'s &3question to &e" + question + "?");
		SafeCrackerInventories.openAdminMenu(player());
		SafeCracker.adminQuestionMap.remove(player());
	}

	@Path("scores")
	@Permission("group.staff")
	void scores() {
		send(PREFIX + "Scores for current event:");
		LinkedHashMap<OfflinePlayer, Integer> scores = playerService.getScores(eventService.getActiveEvent());
		int i = 1;
		for (OfflinePlayer player : scores.keySet()) {
			send("&e" + i++ + ". " + player.getName() + ": &3" + scores.get(player));
		}
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		WorldGuardUtils wgUtils = BearFair20.getWGUtils();
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;
		//if (!wgUtils.isInRegion(event.getClickedBlock().getLocation(), BearFair20.getRegion())) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!sign.getLine(1).equals(StringUtils.colorize("&e[SafeCracker]"))) return;

		SafeCrackerEventService eventService = new SafeCrackerEventService();
		SafeCrackerEvent.SafeCrackerGame game = eventService.getActiveEvent();
		SafeCrackerPlayerService playerService = new SafeCrackerPlayerService();
		SafeCrackerPlayer safeCrackerPlayer = playerService.get(event.getPlayer());

		if (!safeCrackerPlayer.getGames().containsKey(game.getName())) {
			safeCrackerPlayer.send("&7&kasdl &7The safe is warded by some kind of spell. Talk to the supervisor for more information. &7&kasdl");
			return;
		}

		if (safeCrackerPlayer.getGames().get(game.getName()).isFinished()) {
			safeCrackerPlayer.send(SafeCracker.PREFIX + "&cYou have already correctly solved the riddle and finished the game");
			return;
		}

		MenuUtils.openAnvilMenu(event.getPlayer(), "", (player, response) -> {
			if (response.equalsIgnoreCase(game.getAnswer())) {
				int score = (int) Math.abs(Duration.between(LocalDateTime.now(), safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted()).getSeconds() - 1);
				safeCrackerPlayer.getGames().get(game.getName()).setScore(score);
				playerService.save(safeCrackerPlayer);
				Tasks.wait(Time.SECOND.x(10), () -> safeCrackerPlayer.send(SafeCracker.PREFIX + "You correctly solved the riddle. You finished with a score of &e" + safeCrackerPlayer.getGames().get(game.getName()).getScore()));
				Discord.staffLog("```[SafeCracker] " + player.getName() + " - " + StringUtils.timespanDiff(safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted(), LocalDateTime.now()) + "```");
				player.closeInventory();
				complete(player);
			} else {
				player.closeInventory();
				safeCrackerPlayer.send(SafeCracker.PREFIX + "&c" + RandomUtils.randomElement(SafeCracker.wrongResponses));
			}
			return AnvilGUI.Response.text(response);
		}, HumanEntity::closeInventory);

	}

	public void complete(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .5f, .01f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(1), 1, false, false));
		player.teleport(new Location(Bukkit.getWorld("safepvp"), -985.5, 110, -1616.5));
		player.sendMessage(StringUtils.colorize("&7&kasdl &eThe safe draws you inside. It is larger on the inside than it appears. &7&kasdl"));
	}

}
