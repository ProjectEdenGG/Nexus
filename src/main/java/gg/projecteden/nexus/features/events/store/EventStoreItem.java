package gg.projecteden.nexus.features.events.store;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.events.store.providers.EventStoreProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreEmojiHatProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreParticlesProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreWingsProvider;
import gg.projecteden.nexus.features.particles.effects.WingsEffect.WingStyle;
import gg.projecteden.nexus.features.store.perks.emojihats.EmojiHat;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.contributor.ContributorService;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommand;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsOp;
import static gg.projecteden.utils.StringUtils.camelCase;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;
import static gg.projecteden.utils.StringUtils.prettyMoney;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EventStoreItem {
	IMAGES(-1, Material.PAINTING) {
		@Override
		public List<String> getLore() {
			return List.of("&eChoose from over 60 pre-imported images or request your own to hang on your wall");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			player.closeInventory();
			PlayerUtils.runCommand(player, "warp images");
		}
	},
	HEADS(50, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return ItemBuilder.fromHeadId("2669");
		}

		@Override
		public List<String> getLore() {
			return List.of("&eChoose from over 25,000 heads to decorate your builds");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			runCommandAsOp(player, "hdb");
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
			return List.of("&eAnimated heads to express your emotions!");
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
			return List.of("&eCustomizable particle shapes and designs");
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
			return List.of("&eParticle wings with customizable styles and colors");
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
	CHAT_EMOJIS(5, Material.MAP, 1000) {
		@Override
		public List<String> getLore() {
			return List.of("&eSend custom emojis in chat!", "&f👀 💯 🔥 👍 👏 😍 😎");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			PlayerUtils.send(player, STORE_PREFIX + "Send custom emojis in chat! &f👀 💯 🔥 👍 👏 😍 😎");
			PlayerUtils.send(player, STORE_PREFIX + new JsonBuilder("Browse the &c/emoji store")
				.command("/emoji store")
				.hover("Click to open the Emoji store")
				.group()
				.next("&3, or request your own via &c/ticket or &#5865F2#questions"));
		}
	},
	SONGS(200, Material.JUKEBOX) {
		@Override
		protected List<String> getLore() {
			return List.of("&ePlay custom noteblock songs from anywhere");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			runCommand(player, "jukebox store");
			player.closeInventory();
		}
	},
	STORE_CREDIT(-1, Material.PAPER) {
		private static final int TOKENS_PER_USD = 100;

		@Override
		protected List<String> getLore() {
			return List.of("&eConvert Event Tokens into coupons for the &c/store");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			Nexus.getSignMenuFactory()
				.lines("", "Enter the amount", "of credit you", "want ($USD)")
				.prefix(STORE_PREFIX)
				.response(lines -> {
					String line = lines[0];
					if (isNullOrEmpty(line)) {
						new EventStoreProvider().open(player);
						return;
					}

					final double usd = convertToUSD(line);
					final int price = (int) (usd * TOKENS_PER_USD);

					new EventUserService().edit(player, user -> user.charge(price));
					new ContributorService().edit(player, user -> user.giveCredit(usd));

					PlayerUtils.send(player, STORE_PREFIX + "You have purchased &e" + prettyMoney(usd) + " store credit. Manage with &c/store credit");
				})
				.open(player);
		}

		private double convertToUSD(String line) {
			line = StringUtils.asParsableDecimal(line);
			if (!Utils.isDouble(line))
				throw new InvalidInputException(line + " is not a valid number");

			final double input = BigDecimal.valueOf(Double.parseDouble(line)).setScale(2, RoundingMode.HALF_UP).doubleValue();
			return Math.ceil(input * 2) / 2;
		}
	},
	;

	private final int price;
	private final Material material;
	private int customModelData;

	protected List<String> getLore() {
		return null;
	}

	@NotNull
	public ItemBuilder getRawDisplayItem() {
		return new ItemBuilder(material).customModelData(customModelData);
	}

	public ItemBuilder getDisplayItem() {
		return getRawDisplayItem().name(camelCase(name())).lore(getLore()).itemFlags(ItemFlag.HIDE_ATTRIBUTES);
	}

	public boolean canView(Player player) {
		return true;
	}

	public abstract void onClick(Player player, EventStoreMenu currentMenu);
}
