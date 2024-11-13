package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.hub.HubTreasureHunter;
import gg.projecteden.nexus.models.hub.HubTreasureHunterService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class HubTreasureHunt implements Listener {
	private static final String HEAD_ID = "13379";
	static final int TOTAL_TREASURE_CHESTS = 25;

	public HubTreasureHunt() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Player player = event.getPlayer();
		debug(player, "PlayerInteractEvent: ");

		if (Hub.isNotAtHub(player)) {
			debug(player, "<- Not at hub");
			return;
		}

		debug(player, "- At hub");

		if (!ActionGroup.CLICK_BLOCK.applies(event)) {
			debug(player, "<- Action != CLICK_BLOCK");
			return;
		}

		debug(player, "- Action == CLICK_BLOCK");

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.PLAYER_HEAD) {
			debug(player, "<- Clicked Block != PLAYER_HEAD");
			return;
		}

		debug(player, "- Clicked Block == PLAYER_HEAD");

		String id = Nexus.getHeadAPI().getItemID(ItemUtils.getItem(block));
		if (!HEAD_ID.equals(id)) {
			debug(player, "<- Head ID " + id + " != " + HEAD_ID);
			return;
		}

		debug(player, "- Matching Head ID");

		final String PREFIX = Features.get(Hub.class).getPrefix();
		final HubTreasureHunterService service = new HubTreasureHunterService();
		final HubTreasureHunter hunter = service.get(player);
		Location location = block.getLocation();
		if (hunter.getFound().contains(location)) {
			PlayerUtils.send(player, PREFIX + "&cYou already found that treasure chest");
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(0.5).play();
			return;
		}

		hunter.getFound().add(location);
		service.save(hunter);

		new ParticleBuilder(Particle.VILLAGER_HAPPY).location(location.toCenterLocation()).offset(0.25, 0.25, 0.25).count(15).spawn();

		final int found = hunter.getFound().size();
		if (found != TOTAL_TREASURE_CHESTS) {
			PlayerUtils.send(player, PREFIX + "You found a treasure chest &7(%s/%s)".formatted(found, TOTAL_TREASURE_CHESTS));
			new SoundBuilder(Sound.ENTITY_PLAYER_LEVELUP).receiver(player).volume(0.5).pitch(2.0).play();
			return;
		}

		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).receiver(player).volume(0.5).play();
		PlayerUtils.send(player, PREFIX + "You found all the treasure chests!");
		new EventUserService().edit(player, user -> user.giveTokens(100));
	}

	public static void debug(Player player, String debug) {
		if (!HubCommand.debuggers.contains(player))
			return;

		player.sendMessage(debug);
	}

}
