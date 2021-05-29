package me.pugabyte.nexus.features.events.store;

import fr.minuskube.inv.content.SlotPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.store.providers.EventStoreMenu;
import me.pugabyte.nexus.features.events.store.providers.purchasable.EventStoreEmojiHatProvider;
import me.pugabyte.nexus.features.events.store.providers.purchasable.EventStoreParticlesProvider;
import me.pugabyte.nexus.features.events.store.providers.purchasable.EventStoreWingsProvider;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommand;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Purchasable {
	PAINTINGS(1, 1, 999, Material.PAINTING) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {

		}
	},
	IMAGES(1, 3, 999, Material.FLOWER_BANNER_PATTERN) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {

		}
	},
	HEADS(1, 5, 50, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return new ItemBuilder(Nexus.getHeadAPI().getItemHead("2669"));
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			runCommand(player, "hdb");
		}
	},
	EMOJI_HATS(1, 7, 75, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return new ItemBuilder(Nexus.getHeadAPI().getItemHead("537"));
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreEmojiHatProvider(currentMenu).open(player);
		}
	},
	PARTICLES(3, 0, 75, Material.REDSTONE) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreParticlesProvider(currentMenu).open(player);
		}
	},
	WINGS(3, 2, 75, Material.ELYTRA) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreWingsProvider(currentMenu).open(player);
		}
	},
	CHAT_EMOTES(3, 4, 999, Material.PAPER) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {

		}
	},
	SONGS(3, 6, 999, Material.JUKEBOX) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {

		}
	},
	STORE_CREDIT(3, 8, 999, Material.PAPER) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {

		}
	},
	;

	private final int row, column, price;
	private final Material material;
	private int customModelData;

	public SlotPos getSlot() {
		return SlotPos.of(row, column);
	}

	@NotNull
	public ItemBuilder getRawDisplayItem() {
		return new ItemBuilder(material).customModelData(customModelData);
	}

	public ItemBuilder getDisplayItem() {
		return getRawDisplayItem().name(camelCase(name())).itemFlags(ItemFlag.HIDE_ATTRIBUTES);
	}

	public abstract void onClick(Player player, EventStoreMenu currentMenu);
}
