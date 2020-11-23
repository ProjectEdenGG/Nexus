package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static me.pugabyte.nexus.utils.BlockUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.getShortLocationString;

// TODO PUGMAS: teleport back to pugmas
@NoArgsConstructor
public class ToyTesting implements Listener {
	private static final String error = "Minigame is full";

	@AllArgsConstructor
	private enum Toy {
		BATTLESHIP(Pugmas20.location(930, 94, 321), "mgm join alphavsomega", Pugmas20User::setBattleship),
		MASTER_MIND(Pugmas20.location(930, 94, 320), "mgm join mastermind", Pugmas20User::setMasterMind),
		CONNECT_4(Pugmas20.location(930, 94, 319), "warp connect4", Pugmas20User::setConnectFour),
		TIC_TAC_TOE(Pugmas20.location(930, 94, 318), "warp tictactoe", Pugmas20User::setTicTacToe);

		@Getter
		private final Location location;
		@Getter
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
		if (isNullOrAir(block)) return;

		Toy toy = Toy.of(block.getLocation());
		Utils.send(player, getShortLocationString(block.getLocation()) + " (" + toy + ")");
		if (toy == null) return;

		Predicate<String> isStarted = name -> MatchManager.get(ArenaManager.get(name)).isStarted();
		if (toy.equals(Toy.BATTLESHIP)) {
			if (isStarted.test("AlphaVsOmega")) {
				Utils.send(player, error);
				return;
			}
		} else if (toy.equals(Toy.MASTER_MIND)) {
			if (isStarted.test("MasterMind")) {
				Utils.send(player, error);
				return;
			}
		}

		Utils.runCommandAsOp(player, toy.getCommand());
	}

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		String arenaName = match.getArena().getName();
		if (arenaName.equalsIgnoreCase("AlphaVsOmega")) {
			setPlayedGame(match.getPlayers(), Toy.BATTLESHIP);
		} else if (arenaName.equalsIgnoreCase("MasterMind")) {
			setPlayedGame(match.getPlayers(), Toy.MASTER_MIND);
		}
	}

	@EventHandler
	public void onPressButton(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Block block = event.getClickedBlock();
		if (isNullOrAir(block)) return;

		if (!MaterialTag.BUTTONS.isTagged(block.getType())) return;

		Player player = event.getPlayer();
		WorldGuardUtils WGUtils = new WorldGuardUtils(player);
		List<Player> players = Collections.singletonList(player);

		if (WGUtils.isInRegion(player, "connect4"))
			setPlayedGame(players, Toy.CONNECT_4);
		else if (WGUtils.isInRegion(player, "tictactoe"))
			setPlayedGame(players, Toy.TIC_TAC_TOE);
	}

	private void setPlayedGame(List<Player> players, Toy toy) {
		Pugmas20Service service = new Pugmas20Service();
		for (Player player : players) {
			Pugmas20User user = service.get(player);

			if (!user.getToyTestingStage().equals(QuestStage.STARTED))
				continue;

			toy.getSetter().accept(user, true);
			service.save(user);
		}
	}
}
