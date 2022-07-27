package gg.projecteden.nexus.features.resourcepack;

import com.google.gson.Gson;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser.TitanSettings;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.resourcepack.ResourcePack.isCustomItem;

public class ResourcePackListener implements Listener {

	public ResourcePackListener() {
		Nexus.registerListener(this);

		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), "titan:serverbound", new VersionsChannelListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(Nexus.getInstance(), "titan:clientbound");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Tasks.wait(TickTime.SECOND.x(2), () -> {
			ResourcePack.send(player);

			// Try Again if failed
			Tasks.wait(TickTime.SECOND.x(5), () -> {
				if (Status.FAILED_DOWNLOAD == player.getResourcePackStatus())
					ResourcePack.send(player);
			});
		});
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		final CustomMaterial customMaterial = CustomMaterial.of(event.getItemInHand());
		if (customMaterial != null && customMaterial.canBePlaced())
			return;

		if (isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame)
			return;

		if (isCustomItem(ItemUtils.getTool(event.getPlayer())))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerFlowerPotManipulateEvent event) {
		if (isCustomItem(event.getItem()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (!Rank.of(event.getPlayer()).isAdmin())
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final ItemStack item = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
		if (ModelId.of(item) == 0)
			return;

		if (!(event.getRightClicked() instanceof ArmorStand armorStand))
			return;

		final ItemStack existing = armorStand.getItem(EquipmentSlot.HEAD);
		armorStand.setItem(EquipmentSlot.HEAD, item);
		event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, existing);
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("Resource Pack Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());
	}

	public static class VersionsChannelListener implements PluginMessageListener {
		
		@Override
		public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
			String stringMessage = new String(message);
			TitanSettings settings = new Gson().fromJson(stringMessage, TitanSettings.class);

			new LocalResourcePackUserService().edit(player, user -> {
				user.setTitanSettings(settings);
				Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + user.getSaturnVersion() + " Titan: " + user.getTitanVersion());
			});
		}

	}

}
