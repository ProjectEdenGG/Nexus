package me.pugabyte.bncore.features.menus.rewardchests;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.votes.mysterychest.MysteryChest;
import me.pugabyte.bncore.models.mysterychest.MysteryChestPlayer;
import me.pugabyte.bncore.models.mysterychest.MysteryChestService;
import me.pugabyte.bncore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

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

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		MysteryChestService service = new MysteryChestService();
		MysteryChestPlayer mysteryChestPlayer = service.get(event.getPlayer());
		for (RewardChestType type : mysteryChestPlayer.getAmounts().keySet()) {
			if (mysteryChestPlayer.getAmounts().get(type) > 0) {
				ItemStack item = type.getItem().clone();
				item.setAmount(mysteryChestPlayer.getAmounts().get(type));
				Tasks.wait(Time.SECOND.x(10), () -> {
					if (event.getPlayer().isOnline()) {
						Utils.giveItem(event.getPlayer(), item);
						event.getPlayer().sendMessage(StringUtils.colorize("&3You have been given &e" +
								mysteryChestPlayer.getAmounts().get(type) + " " + StringUtils.camelCase(type.name()) +
								" Chest Keys. &3Use them at spawn at the &eMystery Chest"));
						SoundUtils.Jingle.PING.play(event.getPlayer());
						mysteryChestPlayer.getAmounts().remove(type);
						service.save(mysteryChestPlayer);
					}
				});
			}
		}
	}

}
