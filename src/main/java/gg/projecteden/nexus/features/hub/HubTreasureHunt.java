package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.hub.HubTreasureHunter;
import gg.projecteden.nexus.models.hub.HubTreasureHunterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static gg.projecteden.nexus.utils.BlockUtils.isNullOrAir;

public class HubTreasureHunt implements Listener {
	private static final String HEAD_ID = "13379";
	private static final int TOTAL_TREASURE_CHESTS = 16;

	public HubTreasureHunt() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final String prefix = Features.get(Hub.class).getPrefix();
		final Player player = event.getPlayer();
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.PLAYER_HEAD)
			return;

		if (!HEAD_ID.equals(Nexus.getHeadAPI().getBlockID(block)))
			return;

		final HubTreasureHunterService service = new HubTreasureHunterService();
		final HubTreasureHunter hunter = service.get(player);
		if (hunter.getFound().contains(block.getLocation())) {
			PlayerUtils.send(player, prefix + "&cYou already found that treasure chest");
			return;
		}

		hunter.getFound().add(block.getLocation());
		service.save(hunter);
		final int found = hunter.getFound().size();
		if (found != TOTAL_TREASURE_CHESTS) {
			PlayerUtils.send(player, prefix + "You found a treasure chest &7(%s/%s)".formatted(found, TOTAL_TREASURE_CHESTS));
			return;
		}

		PlayerUtils.send(player, prefix + "You found all the treasure chests!");
		// TODO Reward
	}

}
