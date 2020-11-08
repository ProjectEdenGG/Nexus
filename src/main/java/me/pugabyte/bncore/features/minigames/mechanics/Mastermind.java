package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.matchdata.MastermindMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Regenerating({"board", "guess"})
public final class Mastermind extends SingleplayerMechanic {

	@Override
	public String getName() {
		return "Mastermind";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.TRIPWIRE_HOOK);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		MastermindMatchData matchData = match.getMatchData();
	}

	public void removeBlock(Minigamer minigamer, Block block) {
		if (!canBuild(minigamer, block)) return;
		ItemStack item = new ItemStack(block.getType());
		block.setType(Material.AIR);
		Player player = minigamer.getPlayer();
		player.getInventory().addItem(item);
		player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10F, 1F);
	}

	public boolean canBuild(Minigamer minigamer, Block block) {
		return !minigamer.getMatch().getArena().getRegionsLikeAt("guess", block.getLocation()).isEmpty();
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (EquipmentSlot.HAND != event.getHand()) return;
		if (block == null || !MaterialTag.BUTTONS.isTagged(block.getType())) return;

		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
			event.setCancelled(true);
			removeBlock(minigamer, block);
			return;
		}

		if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
			Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
			if (!Utils.isNullOrAir(event.getItem()) && !canBuild(minigamer, placed))
				event.setCancelled(true);
			return;
		}

		if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
			Match match = minigamer.getMatch();
			if (match.getArena().getRegionsLikeAt("button", block.getLocation()).isEmpty()) return;

			MastermindMatchData matchData = match.getMatchData();

			// TODO Cleanup
			try {
				matchData.guess();
			} catch (MinigameException ex) {
				minigamer.tell("&cError: " + ex.getMessage());
			} catch (Exception ex) {
				minigamer.tell("&cUnknown error occurred");
			}
		}
	}

}
