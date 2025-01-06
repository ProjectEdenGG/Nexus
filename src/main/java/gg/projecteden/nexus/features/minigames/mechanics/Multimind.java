package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.exceptions.MinigameException;
import gg.projecteden.nexus.features.minigames.models.matchdata.IMastermindMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.MultimindMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// TODO Lobby

@Regenerating({"board", "guess"})
public final class Multimind extends SingleplayerMechanic {

	@Override
	public @NotNull String getName() {
		return "Mastermind Duel";
	}

	@Override
	public @NotNull String getDescription() {
		return "Race with a friend to complete the crypic 4-color puzzle";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.TRIPWIRE_HOOK);
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		MultimindMatchData matchData = match.getMatchData();
		match.getAliveMinigamers().forEach(matchData::giveLoadout);
		matchData.resetResultsSign();
	}

	public void removeBlock(Minigamer minigamer, Block block) {
		if (!canBuild(minigamer, block)) return;
		ItemStack item = new ItemStack(block.getType());
		block.setType(Material.AIR);
		Player player = minigamer.getOnlinePlayer();
		player.getInventory().addItem(item);
		player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10F, 1F);
	}

	public boolean canBuild(Minigamer minigamer, Block block) {
		return isInRegion(minigamer.getMatch(), block, "guess") && ((IMastermindMatchData) minigamer.getMatch().getMatchData()).canGuess(minigamer);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (EquipmentSlot.HAND != event.getHand() || block == null) return;

		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
			event.setCancelled(true);
			removeBlock(minigamer, block);
			return;
		}

		if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
			Match match = minigamer.getMatch();
			MultimindMatchData matchData = match.getMatchData();
			if (MaterialTag.BUTTONS.isTagged(block.getType())) {
				// TODO Cleanup
				try {
					matchData.guess(minigamer);
				} catch (MinigameException ex) {
					minigamer.tell("&cError: " + ex.getMessage());
				} catch (Exception ex) {
					minigamer.tell("&cUnknown error occurred");
					ex.printStackTrace();
				}
				return;
			}

			if (MaterialTag.SIGNS.isTagged(block.getType())) {
				int guess = matchData.getGuess(minigamer);
				Sign sign = (Sign) block.getState();

				String line1 = StringUtils.stripColor(sign.getLine(0));
				if ("< Colorblind >".equals(line1)) {
					if (guess != 1) {
						minigamer.tell("You cannot change colorblind mode in the middle of the game");
						return;
					}
					matchData.toggleColorblind(minigamer);
					matchData.giveLoadout(minigamer);
					matchData.createAnswer();
					match.getArena().regenerate();
					return;
				}
				if ("< Difficulty >".equals(line1)) {
					if (guess != 1) {
						minigamer.tell("You cannot change the difficulty mode in the middle of the game");
						return;
					}
					String line2 = StringUtils.stripColor(sign.getLine(1));
					if ("Easy".equals(line2))
						matchData.setRepeats(false);
					else if ("Hard".equals(line2))
						matchData.setRepeats(true);
					matchData.createAnswer();
					match.getArena().regenerate();
					minigamer.tell("Difficulty mode updated");
					return;
				}
			}

			Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
			if (!Nullables.isNullOrAir(event.getItem()) && !canBuild(minigamer, placed))
				event.setCancelled(true);
		}
	}

}
