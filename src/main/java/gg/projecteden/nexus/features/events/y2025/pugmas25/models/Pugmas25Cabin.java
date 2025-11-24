package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25Command;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Cabin implements Listener {
	public static String DOOR_REGION = "pugmas25_cabin_door";

	private Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Cabin() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
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

		event.setCancelled(true);
		PlayerUtils.runCommand(event.getPlayer(), "pugmas25 advent");
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (!event.getRegion().getId().equals(DOOR_REGION))
			return;

		Pugmas25UserService userService = new Pugmas25UserService();
		Pugmas25User user = userService.get(player);

		if (user.isUnlockedCabin())
			return;

		event.setCancelled(true);
		if (!CooldownService.isOnCooldown(player.getUniqueId(), "pugmas25_cabin_locked", TickTime.SECOND.x(2)))
			user.sendMessage(Pugmas25.PREFIX + "&cYou cannot enter this cabin right now");
	}
}
