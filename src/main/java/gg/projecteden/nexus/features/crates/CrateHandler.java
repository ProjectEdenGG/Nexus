package gg.projecteden.nexus.features.crates;

import gg.projecteden.crates.models.CrateAnimation;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Data
public class  CrateHandler {

	public static final Map<UUID, CrateAnimation> ANIMATIONS = new HashMap<>();

	public static void openCrate(CrateType type, Entity entity, Player player, int amount) {
		if (isInUse(entity)) return;

		CrateLoot loot = pickCrateLoot(type);
		if (!canHoldItems(player, loot))
			return;

		CrateAnimation animation;
		ItemStack itemstack = amount > 1 ? new ItemStack(Material.DIAMOND) : loot.getDisplayItem();
		String displayName = amount > 1 ? "&3Multiple Rewards" : loot.getDisplayName();
		try {
			BiFunction<Location, Consumer<Item>, Item> func = (location, item) -> location.getWorld().dropItem(location, itemstack, item2 -> {
				item.accept(item2);
				item2.customName(new JsonBuilder(displayName).build());
				item2.setCustomNameVisible(true);
			});
			Constructor<?> constructor = type.getAnimationClass().getConstructor(Entity.class, BiFunction.class);
			animation = (CrateAnimation) constructor.newInstance(entity, func);
		} catch (Exception e) {
			throw new CrateOpeningException("Unable to start animation");
		}

		AtomicInteger amountRemaining = new AtomicInteger(amount);

		CrateRecapBook recap = new CrateRecapBook(type);
		ANIMATIONS.put(entity.getUniqueId(), animation);
		animation.play().thenRun(() -> {
			takeKey(type, player);
			amountRemaining.decrementAndGet();
			recap.add(loot);
			giveItems(player, loot);
			ANIMATIONS.put(entity.getUniqueId(), null);

			while (amountRemaining.getAndDecrement() > 0) {
				CrateLoot _loot = pickCrateLoot(type);
				if (!canHoldItems(player, _loot))
					break;
				takeKey(type, player);
				recap.add(_loot);
				giveItems(player, _loot);
			}

			if (amount > 1)
				recap.open(player);
		});
	}

	private static boolean isInUse(Entity entity) {
		if (!ANIMATIONS.containsKey(entity.getUniqueId()))
			return false;
		return ANIMATIONS.get(entity.getUniqueId()).isActive();
	}

	private static boolean canHoldItems(Player player, CrateLoot loot) {
		if (!PlayerUtils.hasRoomFor(player, loot.getItems().toArray(ItemStack[]::new))) {
			PlayerUtils.send(player, Crates.PREFIX + "Please clear room in your inventory before continuing to open crates");
			return false;
		}
		return true;
	}

	private static CrateLoot pickCrateLoot(CrateType type) {
		Map<CrateLoot, Double> original = new HashMap<>();
		Crates.getLootByType(type).stream()
			.filter(CrateLoot::isActive)
			.forEach(crateLoot -> original.put(crateLoot, crateLoot.getWeight()));

		if (original.isEmpty())
			throw new CrateOpeningException("&3Coming soon...");

		return RandomUtils.getWeightedRandom(original);
	}

	private static void giveItems(Player player, CrateLoot loot) {
		PlayerUtils.giveItems(player, loot.getItems());
		if (!isNullOrEmpty(loot.getCommandsNoSlash()))
			loot.getCommandsNoSlash().forEach(command -> PlayerUtils.runCommandAsConsole(command.replaceAll("%player%", player.getName())));
		if (loot.isShouldAnnounce())
			Chat.Broadcast.all()
				.prefix(Crates.PREFIX)
				.muteMenuItem(MuteMenuItem.CRATES)
				.message("&e" + Nickname.of(player) + " &3has received a &e" + loot.getTitle() + " &3from the &e" + camelCase(loot.getType()))
				.send();
	}

	private static void takeKey(CrateType type, Player player) {
		try {
			boolean took = false;
			ItemStack key = type.getKey();
			for (ItemStack item : player.getInventory().getContents()) {
				if (isNullOrAir(item)) continue;
				if (ItemUtils.isFuzzyMatch(key, item)) {
					item.setAmount(item.getAmount() - 1);
					took = true;
					break;
				}
			}
			if (!took) throw new CrateOpeningException("no key present");
		} catch (Exception ex) {
			throw new CrateOpeningException("You must have a key in your inventory");
		}
	}

	public static void reset(Entity entity) {
		if (!ANIMATIONS.containsKey(entity.getUniqueId()))
			return;
		ANIMATIONS.get(entity.getUniqueId()).reset();
	}
}
