package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Cabin implements Listener {
	public static String CABIN_REGION = "pugmas25_cabin";
	public static String DOOR_REGION = "pugmas25_cabin_door";

	private final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Cabin() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onClickBed(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || !MaterialTag.BEDS.isTagged(block))
			return;

		var pugmas = Pugmas25.get();
		if (!pugmas.isAtEvent(event))
			return;

		if (!pugmas.worldguard().isInRegion(event.getPlayer(), CABIN_REGION))
			return;

		Pugmas25User user = userService.get(event.getPlayer());
		if (!user.isUnlockedCabin())
			return;

		event.setCancelled(true);
		user.setSpawnLocation(event.getPlayer().getLocation());
		user.sendMessage(Pugmas25.PREFIX + "Respawn location set to current position");
	}

	@EventHandler
	public void onClickCalendar(PlayerInteractEntityEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		ItemStack itemStack = itemFrame.getItem();
		if (Nullables.isNullOrAir(itemStack))
			return;

		if (!ItemModelType.PUGMAS25_CALENDAR.is(itemStack))
			return;

		if (WorldGuardEditCommand.isEnabled(event.getPlayer()))
			return;

		Pugmas25User user = userService.get(event.getPlayer());
		if (!user.isUnlockedCabin())
			return;

		event.setCancelled(true);
		PlayerUtils.runCommand(event.getPlayer(), "pugmas25 advent");
	}

	@EventHandler
	public void onEnterCabin(PlayerEnteringRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (!event.getRegion().getId().equals(DOOR_REGION))
			return;

		Pugmas25User user = userService.get(player);
		if (user.isUnlockedCabin())
			return;

		event.setCancelled(true);
		if (!CooldownService.isOnCooldown(player.getUniqueId(), "pugmas25_cabin_locked", TickTime.SECOND.x(2)))
			user.sendMessage(Pugmas25.PREFIX + "&cYou cannot enter this cabin right now");
	}
}
