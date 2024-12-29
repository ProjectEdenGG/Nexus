package gg.projecteden.nexus.features.events.store;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreEmojiHatProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreParticlesProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreWingsProvider;
import gg.projecteden.nexus.features.particles.effects.WingsEffect.WingStyle;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.store.perks.visuals.EmojiHatsCommand.EmojiHat;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EventStoreItem {
	CUSTOM_PAINTINGS(40, Material.PAINTING) {
		@Override
		public List<String> getLore() {
			return List.of(
				"&eChoose from over 60 pre-imported images or request your own to hang on your wall",
				"",
				"&6Price:&e " + getPrice() + " tokens per frame"
			);
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			player.closeInventory();
			PlayerUtils.runCommand(player, "warp images");
		}
	},
	DECORATION_HEADS(15, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return ItemBuilder.fromHeadId("2669");
		}

		@Override
		public List<String> getLore() {
			return List.of("&eChoose from over 25,000 heads to decorate your builds",
				"",
				"&6Price:&e " + getPrice() + " tokens per head");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			PlayerUtils.runCommandAsOp(player, "hdb");
		}
	},
	EMOJI_HATS(75, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return ItemBuilder.fromHeadId("537");
		}

		@Override
		public List<String> getLore() {
			return List.of("&eAnimated heads to express your emotions!",
				"",
				"&6Price:&e " + getPrice() + " tokens per emote");
		}

		@Override
		public boolean canView(Player player) {
			for (EmojiHat type : EmojiHat.values())
				if (!type.canBeUsedBy(player))
					return true;

			return false;
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreEmojiHatProvider(currentMenu).open(player);
		}
	},
	PARTICLES(75, Material.REDSTONE) {
		@Override
		public List<String> getLore() {
			return List.of("&eCustomizable particle shapes and designs",
				"",
				"&6Price:&e " + getPrice() + " tokens per particle");
		}

		@Override
		public boolean canView(Player player) {
			for (ParticleType type : EnumUtils.valuesExcept(ParticleType.class, ParticleType.WINGS))
				if (!type.canBeUsedBy(player))
					return true;

			return false;
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreParticlesProvider(currentMenu).open(player);
		}
	},
	WINGS(75, Material.ELYTRA) {
		@Override
		public List<String> getLore() {
			return List.of("&eParticle wings with customizable styles and colors",
				"",
				"&6Price:&e " + getPrice() + " tokens per style");
		}

		@Override
		public boolean canView(Player player) {
			for (WingStyle style : WingStyle.values())
				if (!style.canBeUsedBy(player))
					return true;

			return false;
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			new EventStoreWingsProvider(currentMenu).open(player);
		}
	},
	CHAT_EMOJIS(5, CustomMaterial.EMOJI_100) {
		@Override
		public List<String> getLore() {
			return List.of("&eSend custom emojis in chat!", "&f👀 💯 🔥 👍 👏 😍 😎",
				"",
				"&6Price:&e " + getPrice() + " tokens per emoji");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			player.closeInventory();
			PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "Send custom emojis in chat! &f👀 💯 🔥 👍 👏 😍 😎");
			PlayerUtils.send(player, new JsonBuilder(EdenEvent.PREFIX_STORE + "Browse the &c/emoji store")
				.command("/emoji store")
				.hover("Click to open the Emoji store")
				.group()
				.next("&3, or request your own via &c/ticket &3or &#5865F2#questions"));
		}
	},
	SONGS(200, Material.JUKEBOX) {
		@Override
		protected List<String> getLore() {
			return List.of("&ePlay custom noteblock songs from anywhere",
				"",
				"&6Price:&e " + getPrice() + " tokens per song");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			PlayerUtils.runCommand(player, "jukebox store");
			player.closeInventory();
		}
	},
	;

	private final int price;
	private final Material material;
	private int modelId;

	EventStoreItem(int price, CustomMaterial material) {
		this(price, material.getMaterial(), material.getModelId());
	}

	protected List<String> getLore() {
		return null;
	}

	@NotNull
	public ItemBuilder getRawDisplayItem() {
		return new ItemBuilder(material).modelId(modelId);
	}

	public ItemBuilder getDisplayItem() {
		return getRawDisplayItem().name(StringUtils.camelCase(name())).lore(getLore()).itemFlags(ItemFlag.HIDE_ATTRIBUTES);
	}

	public boolean canView(Player player) {
		return true;
	}

	public abstract void onClick(Player player, EventStoreMenu currentMenu);
}
