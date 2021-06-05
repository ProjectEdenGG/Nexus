package me.pugabyte.nexus.features.delivery.providers;

import de.tr7zw.nbtapi.NBTItem;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AddMessageListener implements Listener {
	private static final String NBT_KEY = "DeliveryMessageId";

	private final SendDeliveryMenuProvider menu;
	private final Player player;
	private final UUID uuid = UUID.randomUUID();

	public AddMessageListener(SendDeliveryMenuProvider menu) {
		this.menu = menu;
		this.player = menu.user.getOnlinePlayer();

		Nexus.registerTempListener(this);

		final ItemStack book = new ItemBuilder(Material.WRITABLE_BOOK)
				.name("Sign to Continue")
				.nbt(nbt -> nbt.setString(NBT_KEY, uuid.toString()))
				.build();

		Nexus.log("Giving book to " + player.getName());
		PlayerUtils.giveItem(player, book);
	}

	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent event) {
		if (!event.getPlayer().equals(menu.user.getOnlinePlayer()))
			return;

		final EquipmentSlot hand = ItemUtils.getHandWithTool(event.getPlayer(), Material.WRITABLE_BOOK);
		if (hand == null)
			return;

		final ItemStack book = ItemUtils.getTool(player, hand);
		final NBTItem nbtItem = new NBTItem(book);
		if (!nbtItem.hasKey(NBT_KEY) || !nbtItem.getString(NBT_KEY).equals(uuid.toString()))
			return;

		menu.message = new ItemBuilder(Material.WRITTEN_BOOK)
				.bookMeta(event.getNewBookMeta())
				.name(event.getNewBookMeta().getTitle())
				.build();

		event.setCancelled(true);
//		System.out.println(player.getInventory().getItem(hand));
//		player.getInventory().setItem(hand, null);
		player.getInventory().removeItem(book);
//		player.updateInventory();
//		System.out.println(player.getInventory().getItem(hand));
//		Tasks.wait(1, () -> System.out.println(player.getInventory().getItem(hand)));

		Tasks.wait(Time.SECOND.x(5), () -> menu.open(player));
		Nexus.unregisterTempListener(this);
	}

}
