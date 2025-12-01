package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25GiftGiverReward;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Pugmas25GiftGiver implements Listener {
	private static final NamespacedKey PLAYER_HISTORY_KEY = new NamespacedKey(Nexus.getInstance(), "player_history");

	public Pugmas25GiftGiver() {
		Nexus.registerListener(this);
	}

	public static ItemStack getGift(Player player, int finalAmount) {
		var user = new Pugmas25UserService().get(player);
		return getGift(List.of(user)).amount(finalAmount).build();
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!(event.getRightClicked() instanceof Player recipient))
			return;

		var tool = player.getInventory().getItemInMainHand();
		if (!ItemModelType.PUGMAS_GIFT.is(tool))
			return;

		transferGift(tool, player, recipient);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		var tool = event.getItem();
		if (!ItemModelType.PUGMAS_GIFT.is(tool))
			return;

		var target = player.getTargetEntity(7);
		if (target != null)
			return;

		ConfirmationMenu.builder()
			.title("Claim Gift?")
			.confirmText("&aClaim this gift?")
			.confirmLore(List.of("&fGifts that have been given to", "&fmore people give better rewards!"))
			.onConfirm(e -> {
				PlayerUtils.mailItems(player, Pugmas25GiftGiverReward.of(tool).getRandomItems(), "Pugmas25 Gift", WorldGroup.SURVIVAL);
				PlayerUtils.send(player, Pugmas25.PREFIX + "&3Your gift has been mailed to your Survival inbox!");
				tool.subtract();
			})
			.open(player);
	}

	public static void transferGift(ItemStack item, Player from, Player to) {
		try {
			if (!PlayerUtils.hasRoomFor(to, item)) {
				PlayerUtils.send(from, Pugmas25.PREFIX + "&c" + Nickname.of(to) + " doesn't have enough room in their inventory to receive this gift");
				PlayerUtils.send(to, Pugmas25.PREFIX + "&c" + Nickname.of(from) + " is trying to give you a gift, but you don't have enough room in your inventory");
				return;
			}

			var fromUser = new Pugmas25UserService().get(from);
			var history = getPlayerHistory(item);

			if (history.contains(fromUser)) {
				PlayerUtils.send(from, Pugmas25.PREFIX + "&c" + Nickname.of(to) + " has already received that gift!");
				return;
			}

			history.add(fromUser);
			item.subtract();

			PlayerUtils.giveItem(to, getGift(history).build());
			PlayerUtils.send(from, Pugmas25.PREFIX + "&3You gave a gift to " + Nickname.of(to));
			PlayerUtils.send(to, Pugmas25.PREFIX + "&3" + Nickname.of(from) + " gave you a gift!");
		} catch (Exception ex) {
			MenuUtils.handleException(from, Pugmas25.PREFIX, ex);
		}
	}

	private static @NotNull ItemBuilder getGift(List<Pugmas25User> history) {
		var builder = Pugmas25QuestItem.GIFT.getItemBuilder();
		builder.lore("");
		builder.lore("&fThis gift has already been given to:");
		for (Pugmas25User recipient : history)
			builder.lore("&7- &e" + recipient.getNickname());

		builder.pdc(pdc -> pdc.set(PLAYER_HISTORY_KEY, PersistentDataType.STRING, createPlayerHistory(history)));
		return builder;
	}

	public static List<Pugmas25User> getPlayerHistory(ItemStack item) {
		var string = item.getPersistentDataContainer().get(PLAYER_HISTORY_KEY, PersistentDataType.STRING);
		if (string == null)
			return new ArrayList<>();

		return Arrays.stream(string.split(","))
			.distinct()
			.map(UUID::fromString)
			.map(uuid -> new Pugmas25UserService().get(uuid))
			.collect(Collectors.toList());
	}

	public static String createPlayerHistory(List<Pugmas25User> users) {
		return String.join(",", users.stream().map(Pugmas25User::getUniqueId).map(UUID::toString).toList());
	}
}
