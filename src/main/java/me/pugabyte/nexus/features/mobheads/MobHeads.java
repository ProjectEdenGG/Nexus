package me.pugabyte.nexus.features.mobheads;

import eden.annotations.Environments;
import eden.utils.Env;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.pugabyte.nexus.utils.EntityUtils.isUnnaturalSpawn;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
@Environments(Env.PROD)
public class MobHeads extends Feature implements Listener {

	private static final List<UUID> handledEntities = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onKillEntity(EntityDeathEvent event) {
		if (event.isCancelled())
			return;

		LivingEntity victim = event.getEntity();
		Player killer = victim.getKiller();
		if (killer == null) return;

		// TODO: Remove when done
		if (!Dev.WAKKA.is(killer)) return;
		//

		if (WorldGroup.get(killer) != WorldGroup.SURVIVAL) return;
		if (isUnnaturalSpawn(victim)) return;
		if (isBaby(victim)) return;
		if (handledEntities.contains(victim.getUniqueId())) return;
		handledEntities.add(victim.getUniqueId());

		EntityType type = victim.getType();
		MobHeadType mobHeadType = MobHeadType.of(type);
		ItemStack skull = MobHeadType.getSkull(victim);

		if (isNullOrAir(skull))
			return;

		if (victim instanceof Player)
			skull = new ItemBuilder(skull).name("&e" + ((Player) victim).getDisplayName() + "'s Head").skullOwner((OfflinePlayer) victim).build();

		if (skull != null && RandomUtils.chanceOf(mobHeadType.getChance()))
			killer.getWorld().dropItemNaturally(victim.getLocation(), skull);
	}

	private static boolean isBaby(LivingEntity entity) {
		if (entity instanceof Ageable) {
			Ageable ageable = (Ageable) entity;
			return !ageable.isAdult();
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickupPlayerSkull(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		// TODO: Remove when done
		if (!Dev.WAKKA.is(player))
			return;
		//

		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		if (!MaterialTag.SKULLS.isTagged(itemStack.getType()))
			return;

		UUID skullOwner = ItemUtils.getSkullOwner(itemStack);
		if (skullOwner != null) {
			for (ItemStack mobHead : MobHeadType.getAllSkulls()) {
				if (!MaterialTag.SKULLS.isTagged(mobHead.getType()))
					continue;

				UUID mobOwner = ItemUtils.getSkullOwner(mobHead);
				if (mobOwner != null && mobOwner.equals(skullOwner)) {
					item.setItemStack(mobHead.clone());
					item.getItemStack().setAmount(itemStack.getAmount());
					break;
				}
			}
		} else {
			Material itemType = itemStack.getType();
			boolean vanillaSkull = new MaterialTag(MaterialTag.SKULLS).exclude(Material.PLAYER_HEAD).isTagged(itemType);

			// Should only be triggered by player heads, another plugin handles it as needed.
			if (!vanillaSkull)
				return;

			Optional<ItemStack> skull = MobHeadType.getAllSkulls()
					.stream()
					.filter(mobHead -> mobHead.getType().equals(itemType))
					.findFirst();

			if (!skull.isPresent())
				return;

			item.setItemStack(skull.get());
			item.getItemStack().setAmount(itemStack.getAmount());
		}
	}

}
