package me.pugabyte.nexus.features.delivery.providers;

import de.tr7zw.nbtapi.NBTItem;
import eden.utils.Utils;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.listeners.TemporaryListener;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static me.pugabyte.nexus.features.delivery.DeliveryCommand.PREFIX;

public class MailListener implements TemporaryListener {
	private static final String NBT_KEY = "DeliveryMessageId";

	private final SendDeliveryMenuProvider menu;
	@Getter
	private final Player player;
	private final UUID uuid = UUID.randomUUID();

	@Override
	public void unregister() {
		if (!Utils.isNullOrEmpty(menu.items))
			PlayerUtils.giveItems(player, menu.items);
	}

	public MailListener(SendDeliveryMenuProvider menu) {
		this.menu = menu;
		this.player = menu.user.getOnlinePlayer();

		Nexus.registerTemporaryListener(this);

		final ItemStack book = new ItemBuilder(Material.WRITABLE_BOOK)
				.name("Sign to Continue")
				.nbt(nbt -> nbt.setString(NBT_KEY, uuid.toString()))
				.build();

		PlayerUtils.giveItem(player, book);

		PlayerUtils.send(player, PREFIX + "Write your message in the book given to you, and sign it to continue");
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

		Tasks.wait(1, () -> {
			player.getInventory().removeItem(book);
			menu.open(player);
			Nexus.unregisterTemporaryListener(this);
		});
	}

}
