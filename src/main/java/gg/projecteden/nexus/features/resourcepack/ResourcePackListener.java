package gg.projecteden.nexus.features.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
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

		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), "titan:out", new ResourcePackListener.VersionsChannelListener());
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
		if (isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME))
			return;

		if (isCustomItem(ItemUtils.getTool(event.getPlayer())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (!Rank.of(event.getPlayer()).isAdmin())
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final ItemStack item = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
		if (CustomModelData.of(item) == 0)
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
			if (!channel.equalsIgnoreCase("titan:out"))
				return;

			String stringMessage = new String(message);
			JsonObject json = new Gson().fromJson(stringMessage, JsonObject.class);
			String titanVersion = json.has("titan") ? json.get("titan").toString() : null;
			String saturnVersion = json.has("saturn") ? json.get("saturn").toString() : null;

			Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + saturnVersion + " Titan: " + titanVersion);
			new LocalResourcePackUserService().edit(player, user -> {
				user.setSaturnVersion(saturnVersion);
				user.setTitanVersion(titanVersion);
			});
		}

	}

}
