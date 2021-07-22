package gg.projecteden.nexus.features.events.store;

import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.features.events.store.providers.EventStoreProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreEmojiHatProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreParticlesProvider;
import gg.projecteden.nexus.features.events.store.providers.purchasable.EventStoreWingsProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.contributor.Contributor;
import gg.projecteden.nexus.models.contributor.ContributorService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
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

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommand;
import static gg.projecteden.utils.StringUtils.camelCase;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum EventStoreItem {
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
			return ItemBuilder.fromHeadId("2669");
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
			return ItemBuilder.fromHeadId("537");
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
	CHAT_EMOJIS(3, 4, 999, Material.PAPER) {
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

						final double input = Double.parseDouble(line);
						final double scaled = BigDecimal.valueOf(input).setScale(2, RoundingMode.HALF_UP).doubleValue();
						final double usd = Math.ceil(scaled * 2) / 2;
						final int price = (int) (usd * 50);
						final String moneyFormatted = StringUtils.prettyMoney(usd);

						final EventUserService eventUserService = new EventUserService();
						final ContributorService contributorService = new ContributorService();
						final EventUser eventUser = eventUserService.get(player);
						final Contributor contributor = contributorService.get(player);

						if (!eventUser.hasTokens(price))
							throw new InvalidInputException("You do not have enough tokens for " + moneyFormatted);

						eventUser.takeTokens(price);
						eventUserService.save(eventUser);
						contributor.giveCredit(usd);
						contributorService.save(contributor);

						eventUser.sendMessage(STORE_PREFIX + "You have purchased &e" + moneyFormatted + " store " +
								"credit &3with &e" + price + " event tokens&3. Manage with &c/store credit");
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

	@NotNull
	public ItemBuilder getRawDisplayItem() {
		return new ItemBuilder(material).customModelData(customModelData);
	}

	public ItemBuilder getDisplayItem() {
		return getRawDisplayItem().name(camelCase(name())).itemFlags(ItemFlag.HIDE_ATTRIBUTES);
	}

	public abstract void onClick(Player player, EventStoreMenu currentMenu);
}
