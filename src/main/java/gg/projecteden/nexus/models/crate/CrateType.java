package gg.projecteden.nexus.models.crate;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

@Getter
public enum CrateType {
	VOTE(CustomMaterial.CRATE_KEY_VOTE, true),
	WITHER(CustomMaterial.CRATE_KEY_WITHER),
	MYSTERY(CustomMaterial.CRATE_KEY_MYSTERY),
	WEEKLY_WAKKA(CustomMaterial.CRATE_KEY_WAKKA, true),
	MINIGAMES(CustomMaterial.CRATE_KEY_MINIGAMES, true),
	;

	final CustomMaterial customMaterial;
	final boolean enabled;

	CrateType(CustomMaterial customMaterial) {
		this(customMaterial, false);
	}

	CrateType(CustomMaterial customMaterial, boolean enabled) {
		this.customMaterial = customMaterial;
		this.enabled = enabled;
	}

	public ItemStack getOldKey() {
		return new ItemBuilder(Material.TRIPWIRE_HOOK).name("&eCrate Key").glow()
			.lore(" ").lore("&3Type: &e" + StringUtils.camelCase(name()))
			.lore("&7Use me on the Crate at").lore("&7spawn to receive a reward").build();
	}

	public ItemStack getKey() {
		return new ItemBuilder(Material.PAPER)
			.name("&e" + gg.projecteden.api.common.utils.StringUtils.camelCase(this) + " Crate Key")
			.glow()
				.modelId(customMaterial.getModelId())
			.lore("&7Use me at &e/crates &7to receive a reward")
			.build();
	}

	public static CrateType fromEntity(Entity entity) {
		return CrateConfigService.get().getCrateEntities().entrySet().stream()
			.filter(entry -> entry.getValue().contains(entity.getUniqueId()))
			.map(Entry::getKey)
			.findFirst().orElse(null);
	}

	public static CrateType fromKey(ItemStack item) {
		if (Nullables.isNullOrAir(item)) return null;
		for (CrateType type : values())
			if (type.customMaterial.is(item))
				return type;
		return null;
	}

	public static CrateType fromOldKey(ItemStack item) {
		if (Nullables.isNullOrAir(item)) return null;
		for (CrateType type : values())
			if (type.getOldKey().isSimilar(item))
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
		PlayerUtils.giveItem(player, item);
	}

	/*
	 * Currently doesn't do anything besides these 2 settings
	 * Added incase a crate needs to override this default behavior
	 */
	public void handleItem(Item item) {
		item.setGravity(false);
		Tasks.wait(10, () -> item.setCustomNameVisible(true));
	}
}
