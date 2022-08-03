package gg.projecteden.nexus.models.crate;

import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Getter
public enum CrateType {
	VOTE(10000, true) {
		@Override
		public void handleItem(Item item) {
			item.setGravity(false);
			Tasks.wait(10, () -> item.setCustomNameVisible(true));
		}
	},
	WITHER(10001),
	MYSTERY(10002),
	WEEKLY_WAKKA(10003),
	;
	final int modelId;
	final boolean enabled;

	CrateType(int modelId) {
		this(modelId, false);
	}

	CrateType(int modelId, boolean enabled) {
		this.modelId = modelId;
		this.enabled = enabled;
	}

	public ItemStack getKey() {
		return new ItemBuilder(Material.PAPER)
			.name("&eCrate Key")
			.glow()
			.modelId(getModelId())
			.lore("&7Use me &e/crates &7to receive a reward")
			.build();
	}

	public static CrateType fromEntity(Entity entity) {
		return CrateConfigService.get().getCrateEntities().entrySet().stream()
			.filter(entry -> entry.getValue().contains(entity.getUniqueId()))
			.map(Entry::getKey)
			.findFirst().orElse(null);
	}

	public static CrateType fromKey(ItemStack item) {
		if (isNullOrAir(item)) return null;
		for (CrateType type : values())
			if (ItemUtils.isFuzzyMatch(item, type.getKey()))
				return type;
		return null;
	}

	public void give(OfflinePlayer player) {
		give(player, 1);
	}

	public boolean giveVPS(Player player, int amount) {
		ItemStack item = getKey().clone();
		item.setAmount(amount);

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL) {
			PlayerUtils.send(player, "&cYou must be in survival to buy this");
			return false;
		}

		if (!PlayerUtils.hasRoomFor(player.getPlayer(), item)) {
			PlayerUtils.send(player, "&cYou don't have enough space in your inventory");
			return false;
		}

		player.getInventory().addItem(item);
		return true;
	}

	public void give(OfflinePlayer player, int amount) {
		ItemStack item = getKey().clone();
		item.setAmount(amount);
		if (player.isOnline() && player.getPlayer() != null && WorldGroup.of(player.getPlayer()) == WorldGroup.SURVIVAL && PlayerUtils.hasRoomFor(player.getPlayer(), item))
			player.getPlayer().getInventory().addItem(item);
		else {
			Mail.fromServer(player.getUniqueId(), WorldGroup.SURVIVAL, item).send();

			if (player.isOnline()) {
				String error = WorldGroup.of(player.getPlayer()) == WorldGroup.SURVIVAL ? "&3 but your inventory was full. Use &c/delivery &3to claim it." : "&3. Use &c/delivery&3 in the survival world to claim it.";
				PlayerUtils.send(player.getPlayer(), Crates.PREFIX + "You have been given &e" + amount + " " +
						StringUtils.camelCase(name()) + " Crate Key" + ((amount > 1) ? "s" : "") + error);
			}
		}
	}

	public void handleItem(Item item) {
		item.setCustomNameVisible(true);
	}
}
