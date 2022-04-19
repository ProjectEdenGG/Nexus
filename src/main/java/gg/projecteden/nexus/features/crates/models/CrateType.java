package gg.projecteden.nexus.features.crates.models;

import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.crates.crates.BearFair21Crate;
import gg.projecteden.nexus.features.crates.crates.BossCrate;
import gg.projecteden.nexus.features.crates.crates.FebVoteRewardCrate;
import gg.projecteden.nexus.features.crates.crates.MysteryCrate;
import gg.projecteden.nexus.features.crates.crates.Pugmas21Crate;
import gg.projecteden.nexus.features.crates.crates.VoteCrate;
import gg.projecteden.nexus.features.crates.crates.WeeklyWakkaCrate;
import gg.projecteden.nexus.features.crates.menus.CratePreviewProvider;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Getter
public enum CrateType {
	ALL(null, null),
	VOTE(new VoteCrate(), new Location(Bukkit.getWorld("survival"), 8, 15, 11)),
	MYSTERY(new MysteryCrate(), new Location(Bukkit.getWorld("survival"), 11, 15, 8)),
	WEEKLY_WAKKA(new WeeklyWakkaCrate(), new Location(Bukkit.getWorld("survival"), 15, 15, -8)),
	FEB_VOTE_REWARD(new FebVoteRewardCrate(), null),
	BOSS(new BossCrate(), new Location(Bukkit.getWorld("survival"), -9, 15, 12)),
	BEAR_FAIR_21(new BearFair21Crate(), null),
	PUGMAS_21(new Pugmas21Crate(), new Location(Bukkit.getWorld("survival"), -12, 15, 9)),
	;

	Crate crateClass;
	Location location;

	CrateType(Crate crateClass, Location location) {
		this.crateClass = crateClass;
		this.location = location;
	}

	public Location getCenteredLocation() {
		return LocationUtils.getCenteredLocation(this.location.clone());
	}

	private final ItemStack key = new ItemBuilder(Material.TRIPWIRE_HOOK).name("&eCrate Key").glow()
			.lore(" ").lore("&3Type: &e" + StringUtils.camelCase(name()))
			.lore("&7Use me on the Crate at").lore("&7spawn to receive a reward").build();

	public ItemStack getKey() {
		return key.clone();
	}

	public static CrateType fromLocation(Location location) {
		for (CrateType type : values())
			if (location.equals(type.location))
				return type;
		return null;
	}

	public static CrateType fromKey(ItemStack item) {
		if (isNullOrAir(item)) return null;
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

}
