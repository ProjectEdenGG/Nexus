package gg.projecteden.nexus.models.crate;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

@Getter
@AllArgsConstructor
public enum CrateType {
	VOTE(ItemModelType.CRATE_KEY_VOTE, "痪", true),
	WITHER(ItemModelType.CRATE_KEY_WITHER, "囱"),
	MYSTERY(ItemModelType.CRATE_KEY_MYSTERY, "笯", true),
	WEEKLY_WAKKA(ItemModelType.CRATE_KEY_WAKKA, "清",true),
	MINIGAMES(ItemModelType.CRATE_KEY_MINIGAMES, "禘", true),
	ONE_BLOCK(null, "皂"),
	HALLOWEEN(ItemModelType.CRATE_KEY_HALLOWEEN, "灿")
	;

	final ItemModelType itemModelType;
	final String titleCharacter;
	final boolean enabled;

	CrateType(ItemModelType itemModelType, String titleCharacter) {
		this(itemModelType, titleCharacter, false);
	}

	final ItemStack OLD_KEY = new ItemBuilder(Material.TRIPWIRE_HOOK)
		.name("&eCrate Key")
		.glow()
		.lore(" ")
		.lore("&3Type: &e" + StringUtils.camelCase(name()))
		.lore("&7Use me on the Crate at")
		.lore("&7spawn to receive a reward")
		.build();

	public ItemStack getOldKey() {
		return OLD_KEY;
	}

	public ItemStack getKey() {
		return new ItemBuilder(Material.PAPER)
			.name("&e" + gg.projecteden.api.common.utils.StringUtils.camelCase(this) + " Crate Key")
			.glow()
			.model(itemModelType.getModel())
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
			if (type.itemModelType.is(item))
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
		PlayerUtils.giveItemAndMailExcess(player, item, WorldGroup.SURVIVAL);
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
