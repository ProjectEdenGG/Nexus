package gg.projecteden.nexus.features.safecracker;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.safecracker.menus.SafeCrackerInventories;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayer;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayer.Game;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayer.SafeCrackerPlayerNPC;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayerService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.Time;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.BiFunction;

@NoArgsConstructor
@Disabled
public class SafeCrackerCommand extends CustomCommand implements Listener {

	public SafeCrackerEventService eventService = new SafeCrackerEventService();
	public SafeCrackerEvent event = eventService.get0();
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
				player().playSound(location(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
				send("&3" + SafeCracker.playerClickedNPC.get(player()) + " >&e " + RandomUtils.randomElement(SafeCracker.correctResponses));
			} else {
				player().playSound(location(), Sound.ENTITY_VILLAGER_NO, 1F, 2F);
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
//			Discord.staffLog("```[SafeCracker] " + name() + " - " + score + "```");
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

	@Path("scores [page]")
	@Permission("group.staff")
	void scores(@Arg("1") int page) {
		send(PREFIX + "Scores for current event:");
		LinkedHashMap<UUID, Integer> scores = playerService.getScores(eventService.getActiveEvent());
		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) ->
			json(index + Nickname.of(uuid) + ": &3" + scores.get(uuid));

		paginate(Utils.sortByValueReverse(scores).keySet(), formatter, "/safecracker scores", page);
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		WorldGuardUtils wgUtils = BearFair20.getWGUtils();
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
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
			safeCrackerPlayer.sendMessage("&7&kasdl &7The safe is warded by some kind of spell. Talk to the supervisor for more information. &7&kasdl");
			return;
		}

		if (safeCrackerPlayer.getGames().get(game.getName()).isFinished()) {
			safeCrackerPlayer.sendMessage(SafeCracker.PREFIX + "&cYou have already correctly solved the riddle and finished the game");
			return;
		}

		MenuUtils.openAnvilMenu(event.getPlayer(), "", (player, response) -> {
			if (response.equalsIgnoreCase(game.getAnswer())) {
				int score = (int) Math.abs(Duration.between(LocalDateTime.now(), safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted()).getSeconds() - 1);
				safeCrackerPlayer.getGames().get(game.getName()).setScore(score);
				playerService.save(safeCrackerPlayer);
				Tasks.wait(Time.SECOND.x(10), () -> safeCrackerPlayer.sendMessage(SafeCracker.PREFIX + "You correctly solved the riddle. You finished with a score of &e" + safeCrackerPlayer.getGames().get(game.getName()).getScore()));
				Discord.staffLog("```[SafeCracker] " + player.getName() + " - " + Timespan.of(safeCrackerPlayer.getGames().get(eventService.getActiveEvent().getName()).getStarted(), LocalDateTime.now()).format() + "```");
				player.closeInventory();
				complete(player);
			} else {
				player.closeInventory();
				safeCrackerPlayer.sendMessage(SafeCracker.PREFIX + "&c" + RandomUtils.randomElement(SafeCracker.wrongResponses));
			}
			return AnvilGUI.Response.text(response);
		}, HumanEntity::closeInventory);

	}

	public void complete(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .5f, .01f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(1), 1, false, false));
		player.teleport(new Location(Bukkit.getWorld("safepvp"), -985.5, 110, -1616.5));
		send(player, "&7&kasdl &eThe safe draws you inside. It is larger on the inside than it appears. &7&kasdl");
	}

}
