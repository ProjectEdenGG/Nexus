package me.pugabyte.nexus.features.crates.models;

import fr.minuskube.inv.SmartInventory;
import lombok.Getter;
import me.pugabyte.nexus.features.crates.crates.MysteryCrate;
import me.pugabyte.nexus.features.crates.crates.VoteCrate;
import me.pugabyte.nexus.features.crates.menus.CratePreviewProvider;
import me.pugabyte.nexus.models.delivery.Delivery;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

@Getter
public enum CrateType {
	ALL(null, null),
	VOTE(new VoteCrate(), new Location(Bukkit.getWorld("survival"), 8.00, 15.00, 11.00, .00F, .00F)),
	MYSTERY(new MysteryCrate(), new Location(Bukkit.getWorld("survival"), 11.00, 15.00, 8.00, .00F, .00F));

	Crate crateClass;
	Location location;

	CrateType(Crate crateClass, Location location) {
		this.crateClass = crateClass;
		this.location = location;
	}


	public Location getCenteredLocation() {
		return LocationUtils.getCenteredLocation(this.location);
	}

	@Getter
	ItemStack key = new ItemBuilder(Material.TRIPWIRE_HOOK).name("&eCrate Key").glow()
			.lore(" ").lore("&3Type: &e" + StringUtils.camelCase(name()))
			.lore("&7Use me on the Crate at").lore("&7spawn to receive a reward").build();

	public static CrateType fromLocation(Location location) {
		for (CrateType type : values())
			if (location.equals(type.location))
				return type;
		return null;
	}

	public static CrateType fromKey(ItemStack item) {
		if (ItemUtils.isNullOrAir(item)) return null;
		for (CrateType type : values())
			if (ItemUtils.isFuzzyMatch(item, type.getKey()))
				return type;
		return null;
	}

	public SmartInventory previewDrops(CrateLoot loot) {
		return SmartInventory.builder()
				.size(6, 9)
				.provider(new CratePreviewProvider(this, loot))
				.title(StringUtils.camelCase(name()) + " Crate Rewards")
				.build();
	}

	public void give(OfflinePlayer player) {
		give(player, 1);
	}

	public void give(OfflinePlayer player, int amount) {
		ItemStack item = getKey().clone();
		item.setAmount(amount);
		if (player.isOnline() && WorldGroup.get(player.getPlayer()) == WorldGroup.SURVIVAL && PlayerUtils.hasRoomFor(player.getPlayer(), item))
			player.getPlayer().getInventory().addItem(item);
		else {
			DeliveryService service = new DeliveryService();
			Delivery delivery = service.get(player);
			delivery.add(WorldGroup.SURVIVAL, item);
			service.save(delivery);
		}
	}

}
