package me.pugabyte.nexus.features.menus.rewardchests.mysterychest;

import fr.minuskube.inv.SmartInventory;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.rewardchests.RewardChestLoot;
import me.pugabyte.nexus.features.menus.rewardchests.RewardChestType;
import me.pugabyte.nexus.models.mysterychest.MysteryChestPlayer;
import me.pugabyte.nexus.models.mysterychest.MysteryChestService;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;

public class MysteryChest {

	private final MysteryChestService service = new MysteryChestService();
	private final OfflinePlayer player;
	@Getter
	private static final String fileName = "mysteryChestLoot.yml";
	@Getter
	private static YamlConfiguration config;

	static {
		reloadConfig();
	}

	public MysteryChest(OfflinePlayer player) {
		this.player = player;
	}

	public static SmartInventory getInv(Integer id, RewardChestType type) {
		return SmartInventory.builder()
				.title("Mystery Chest Rewards")
				.provider(new MysteryChestEditProvider(id, type))
				.size(id == null ? 6 : 3, 9)
				.build();
	}

	public int give(int amount, RewardChestType type) {
		if (player.isOnline() && WorldGroup.get(player.getPlayer()).equals(WorldGroup.SURVIVAL) && player.getPlayer().getInventory().firstEmpty() != -1) {
			Player onlinePlayer = player.getPlayer();
			ItemStack item = type.getItem().clone();
			item.setAmount(amount);
			ItemUtils.giveItem(onlinePlayer, item);
			PlayerUtils.send(onlinePlayer, "&3You have been given &e" +
					amount + " " + StringUtils.camelCase(type.name()) +
					" Chest Key" + ((amount == 1) ? "" : "s") + ". &3Use them at spawn at the &eMystery Chest");
			SoundUtils.Jingle.PING.play(onlinePlayer);
			return amount;
		} else {
			MysteryChestPlayer mysteryChestPlayer = getMysteryChestPlayer();
			int newAmount = mysteryChestPlayer.getAmounts().getOrDefault(type, 0) + amount;
			mysteryChestPlayer.getAmounts().put(type, newAmount);
			service.save(mysteryChestPlayer);
			return newAmount;
		}
	}

	public int take(int amount, RewardChestType type) {
		MysteryChestPlayer mysteryChestPlayer = getMysteryChestPlayer();
		int newAmount = Math.max(0, mysteryChestPlayer.getAmounts().getOrDefault(type, 0) - amount);
		mysteryChestPlayer.getAmounts().put(type, newAmount);
		service.save(mysteryChestPlayer);
		return newAmount;
	}

	public MysteryChestPlayer getMysteryChestPlayer() {
		return service.get(player);
	}

	public static void reloadConfig() {
		config = Nexus.getConfig(fileName);
	}

	@SneakyThrows
	public static void saveConfig() {
		config.save(Nexus.getFile(fileName));
	}

	public static Set<String> getConfigSections() {
		return config.getKeys(false);
	}

	public static int getNextId() {
		int id = 0;
		Set<String> sections = getConfigSections();
		if (sections.size() == 0) return id;
		for (String section : sections) {
			try {
				int savedId = Integer.parseInt(section);
				if (savedId >= id) id = savedId + 1;
			} catch (Exception ex) {
				Nexus.warn("An error occurred while trying to save a Mystery Chest to file");
				ex.printStackTrace();
			}
		}
		return id;
	}

	public static RewardChestLoot getRewardChestLoot(int id) {
		return (RewardChestLoot) config.get(id + "");
	}

	public static RewardChestLoot[] getAllRewardsByType(RewardChestType type) {
		if (type == RewardChestType.ALL) return getAllRewards();
		return Arrays.stream(getAllRewards()).filter(rewardChestLoot -> rewardChestLoot.getType() == type).toArray(RewardChestLoot[]::new);
	}

	public static RewardChestLoot[] getAllActiveRewardsByType(RewardChestType type) {
		return Arrays.stream(getAllRewardsByType(type)).filter(rewardChestLoot -> rewardChestLoot.isActive()).toArray(RewardChestLoot[]::new);
	}

	public static RewardChestLoot[] getAllRewards() {
		RewardChestLoot[] loot = new RewardChestLoot[getConfigSections().size()];
		int i = 0;
		for (String section : getConfigSections()) {
			RewardChestLoot reward = (RewardChestLoot) config.get(section);
			reward.setId(Integer.parseInt(section));
			loot[i] = reward;
			i++;
		}
		return loot;
	}

}
