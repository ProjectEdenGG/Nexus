package gg.projecteden.nexus.features.events.store;

import fr.minuskube.inv.content.SlotPos;
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
import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsOp;
import static gg.projecteden.utils.StringUtils.camelCase;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EventStoreItem {
	IMAGES(1, 1, -1, Material.PAINTING) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			PlayerUtils.runCommand(player, "warp images");
			// TODO Hologram & way to request image
		}
	},
	HEADS(1, 5, 50, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return ItemBuilder.fromHeadId("2669");
		}

		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			runCommandAsOp(player, "hdb");
		}
	},
	EMOJI_HATS(1, 7, 75, Material.PLAYER_HEAD) {
		@Override
		@NotNull
		public ItemBuilder getRawDisplayItem() {
			return ItemBuilder.fromHeadId("537");
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
	PARTICLES(3, 0, 75, Material.REDSTONE) {
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
	WINGS(3, 2, 75, Material.ELYTRA) {
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
	CHAT_EMOJIS(3, 4, -1, Material.PAPER) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			// TODO
		}
	},
	SONGS(3, 6, -1, Material.JUKEBOX) {
		@Override
		public void onClick(Player player, EventStoreMenu currentMenu) {
			// TODO
		}
	},
	STORE_CREDIT(3, 8, -1, Material.PAPER) {
		private static final int TOKENS_PER_USD = 50;

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

					line = StringUtils.asParsableDecimal(line);
					if (!Utils.isDouble(line))
						throw new InvalidInputException(line + " is not a valid number");

					final double input = BigDecimal.valueOf(Double.parseDouble(line)).setScale(2, RoundingMode.HALF_UP).doubleValue();
					final double usd = Math.ceil(input * 2) / 2;
					final int price = (int) (usd * TOKENS_PER_USD);
					final String moneyFormatted = StringUtils.prettyMoney(usd);

					new EventUserService().edit(player, user -> user.charge(price));
					new ContributorService().edit(player, user -> user.giveCredit(usd));

					PlayerUtils.send(player, STORE_PREFIX + "You have purchased &e" + moneyFormatted + " store credit. Manage with &c/store credit");
				})
				.open(player);
		}
	},
	;

	private final int row, column, price;
	private final Material material;
	private int customModelData;

	public SlotPos getSlot() {
		return SlotPos.of(row, column);
	}

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
