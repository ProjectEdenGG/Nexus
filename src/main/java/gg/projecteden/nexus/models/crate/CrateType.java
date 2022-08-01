package gg.projecteden.nexus.models.crate;

import gg.projecteden.crates.animations.MysteryCrateAnimation;
import gg.projecteden.crates.animations.VoteCrateAnimation;
import gg.projecteden.crates.animations.WeeklyWakkaCrateAnimation;
import gg.projecteden.crates.animations.WitherCrateAnimation;
import gg.projecteden.crates.models.CrateAnimation;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Getter
public enum CrateType {
	VOTE(VoteCrateAnimation.class, 10000),
	WITHER(WitherCrateAnimation.class, 10001) {
		@Override
		public boolean isEnabled() {
			return false;
		}
	},
	MYSTERY(MysteryCrateAnimation.class, 10002) {
		@Override
		public boolean isEnabled() {
			return false;
		}
	},
	WEEKLY_WAKKA(WeeklyWakkaCrateAnimation.class, 10003) {
		@Override
		public boolean isEnabled() {
			return false;
		}
	},
	;

	final Class<? extends CrateAnimation> animationClass;
	final Integer modelId;

	CrateType(Class<? extends CrateAnimation> animationClass, Integer modelId) {
		this.animationClass = animationClass;
		this.modelId = modelId;
	}

	private final ItemStack key = new ItemBuilder(Material.PAPER)
			.name("&eCrate Key")
			.glow()
			.modelId(getModelId())
			.lore("&7Use me &e/crates &7to receive a reward")
			.build();

	public static CrateType fromEntity(Entity entity) {
		return CrateConfigService.get().getCrateEntities().entrySet().stream()
			.filter(entry -> entry.getValue().contains(entity.getUniqueId()))
			.map(Entry::getKey)
			.findFirst().orElse(null);
	}

	public ItemStack getKey() {
		return key.clone();
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

	public boolean isEnabled() {
		return true;
	}

}
