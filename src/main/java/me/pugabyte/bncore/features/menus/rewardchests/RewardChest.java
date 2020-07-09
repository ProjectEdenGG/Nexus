package me.pugabyte.bncore.features.menus.rewardchests;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.votes.mysterychest.MysteryChest;
import me.pugabyte.bncore.models.mysterychest.MysteryChestPlayer;
import me.pugabyte.bncore.models.mysterychest.MysteryChestService;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.Map;

public class RewardChest implements Listener {

	public RewardChest() {
		registerSerializables();
		BNCore.registerListener(this);
	}

	public static SmartInventory getInv(RewardChestLoot... loot) {
		return SmartInventory.builder()
				.size(3, 9)
				.title("Reward Chest")
				.provider(new RewardChestProvider(loot))
				.closeable(false)
				.build();
	}

	private void registerSerializables() {
		new Reflections(this.getClass().getPackage().getName()).getTypesAnnotatedWith(SerializableAs.class).forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

	@EventHandler
	public void onMysteryChestClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == null) return;
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;
		if (event.getClickedBlock().getType() != Material.END_PORTAL_FRAME) return;

		WorldGuardUtils utils = new WorldGuardUtils(Bukkit.getWorld("Survival"));
		if (!utils.isInRegion(event.getClickedBlock().getLocation(), "spawn")) return;

		if (event.getItem() == null) return;
		ItemStack item = event.getItem();
		if (item.getType() != Material.TRIPWIRE_HOOK) return;
		if (item.getLore() == null) return;
		if (!item.getLore().get(1).contains(StringUtils.colorize("&3Type: &e"))) return;

		RewardChestType type;
		try {
			type = RewardChestType.valueOf(item.getLore().get(1).replace(StringUtils.colorize("&3Type: &e"), "").toUpperCase());
		} catch (Exception ignore) {
			return;
		}
		RewardChest.getInv(MysteryChest.getAllActiveRewardsByType(type)).open(event.getPlayer());
		event.getItem().setAmount(event.getItem().getAmount() - 1);
	}

	public void processEvent(PlayerEvent event) {
		Player player = event.getPlayer();
		if (!WorldGroup.get(player).equals(WorldGroup.SURVIVAL)) return;

		MysteryChestService service = new MysteryChestService();
		MysteryChestPlayer mysteryChestPlayer = service.get(player);
		Map<RewardChestType, Integer> amounts = mysteryChestPlayer.getAmounts();

		for (RewardChestType type : amounts.keySet()) {
			int amount = amounts.get(type);
			if (amount > 0) {
				ItemStack item = type.getItem().clone();
				item.setAmount(amount);
				if (player.isOnline()) {
					amounts.remove(type);
					mysteryChestPlayer.setAmounts(amounts);
					service.delete(mysteryChestPlayer);
					service.save(mysteryChestPlayer);

					// this way wont work, will error if player logs out
//					Tasks.wait(Time.SECOND.x(10), () -> {
					Utils.giveItem(player, item);
					player.sendMessage(StringUtils.colorize("&3You have been given &e" +
							amount + " " + StringUtils.camelCase(type.name()) +
							" Chest Key" + ((amount == 1) ? "" : "s") + ". &3Use them at spawn at the &eMystery Chest"));
					SoundUtils.Jingle.PING.play(player);
//					});
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		processEvent(event);
	}

	@EventHandler
	public void onWorldSwitch(PlayerChangedWorldEvent event) {
		processEvent(event);
	}

}
