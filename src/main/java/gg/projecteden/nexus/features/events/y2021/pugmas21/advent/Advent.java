package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Advent21ConfigService;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Advent implements Listener {
	private static final Pugmas21UserService userService = new Pugmas21UserService();

	public Advent() {
		Nexus.registerListener(this);
	}

	static {
		for (Player player : OnlinePlayers.builder().world(Pugmas21.getWorld()).get())
			sendPackets(player);
	}

	private static void sendPackets(Player player) {
		final Pugmas21User user = userService.get(player);
		for (AdventPresent present : Advent21Config.get().getPresents())
			user.advent().show(present);
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		if (Pugmas21.isAtPugmas(event.getPlayer()))
			Tasks.wait(1, () -> sendPackets(event.getPlayer()));
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (Pugmas21.isAtPugmas(event.getPlayer()))
			Tasks.wait(1, () -> sendPackets(event.getPlayer()));
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas21.TODAY.isAfter(Pugmas21.END))
			return;

		final Advent21Config adventConfig = new Advent21ConfigService().get0();
		final AdventPresent present = adventConfig.get(block.getLocation());
		if (present == null)
			return;

		new Pugmas21UserService().edit(player, user -> user.advent().tryCollect(present));
	}

}
