package gg.projecteden.nexus.features.events.y2020.pugmas20.quests;

import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.models.pugmas20.Pugmas20User;
import gg.projecteden.nexus.models.pugmas20.Pugmas20UserService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

// TODO PUGMAS: teleport back to pugmas
@NoArgsConstructor
public class ToyTesting implements Listener {
	private static final String error = "Minigame has already started";
	@Getter
	private static final Location backLocation = Pugmas20.location(931.50, 93.00, 328.50, 180.00F, .00F);

	@AllArgsConstructor
	private enum Toy {
		BATTLESHIP(Pugmas20.location(930, 94, 321), "mgm join alphavsomega", Pugmas20User::setBattleship),
		MASTERMIND(Pugmas20.location(930, 94, 320), "mgm join mastermind", Pugmas20User::setMasterMind),
		CONNECT_4(Pugmas20.location(930, 94, 319), "mgm join connect4", Pugmas20User::setConnectFour),
		TIC_TAC_TOE(Pugmas20.location(930, 94, 318), "mgm join tictactoe", Pugmas20User::setTicTacToe);

		@Getter
		private final Location location;
		private final String command;
		@Getter
		private final BiConsumer<Pugmas20User, Boolean> setter;

		public static Toy of(Location location) {
			for (Toy toy : Toy.values())
				if (toy.getLocation().equals(location))
					return toy;
			return null;
		}

		public String getCommand() {
			return "mcmd warp minigames ;; wait 20 ;; " + command;
		}
	}

	@EventHandler
	public void onClickGameSkull(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block)) return;

		Toy toy = Toy.of(block.getLocation());
		if (toy == null) return;

		Predicate<String> isStarted = name -> MatchManager.get(ArenaManager.get(name)).isStarted();
		if (toy.equals(Toy.BATTLESHIP)) {
			if (isStarted.test("AlphaVsOmega")) {
				PlayerUtils.send(player, error);
				return;
			}
		} else if (toy.equals(Toy.MASTERMIND)) {
			if (isStarted.test("MasterMind")) {
				PlayerUtils.send(player, error);
				return;
			}
		}

		Pugmas20UserService service = new Pugmas20UserService();
		Pugmas20User user = service.get(player);

		List<String> lore = new ArrayList<>();
		if (user.getToyTestingStage() != QuestStage.NOT_STARTED)
			lore.addAll(List.of("", "&fUse &c/pugmas toys &fto", "&freturn to this location"));

		ConfirmationMenu.builder()
			.title("Play " + StringUtils.camelCase(toy) + "?")
				.confirmLore(lore)
				.onConfirm(e -> PlayerUtils.runCommandAsOp(player, toy.getCommand()))
				.open(player);
	}

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		String arenaName = match.getArena().getName();
		if (arenaName.equalsIgnoreCase("AlphaVsOmega")) {
			setPlayedGame(match.getOnlinePlayers(), Toy.BATTLESHIP);
		} else if (arenaName.equalsIgnoreCase("MasterMind")) {
			setPlayedGame(match.getOnlinePlayers(), Toy.MASTERMIND);
		}
	}

	@EventHandler
	public void onPressButton(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block)) return;

		if (!MaterialTag.BUTTONS.isTagged(block.getType())) return;

		Player player = event.getPlayer();
		WorldGuardUtils worldguard = new WorldGuardUtils(player);
		List<Player> players = Collections.singletonList(player);

		if (worldguard.isInRegion(player, "connect4"))
			setPlayedGame(players, Toy.CONNECT_4);
		else if (worldguard.isInRegion(player, "tictactoe"))
			setPlayedGame(players, Toy.TIC_TAC_TOE);
	}

	private void setPlayedGame(List<Player> players, Toy toy) {
		Pugmas20UserService service = new Pugmas20UserService();
		for (Player player : players) {
			Pugmas20User user = service.get(player);
			if (user.getToyTestingStage().equals(QuestStage.STARTED)) {
				toy.getSetter().accept(user, true);
				if (hasPlayedAll(user))
					user.setToyTestingStage(QuestStage.STEPS_DONE);

				service.save(user);
			}
		}
	}

	public boolean hasPlayedAll(Pugmas20User user) {
		return user.isMasterMind() && user.isBattleship() && user.isConnectFour() && user.isTicTacToe();
	}
}
