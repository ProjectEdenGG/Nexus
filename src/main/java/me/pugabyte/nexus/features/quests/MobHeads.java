package me.pugabyte.nexus.features.quests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.PlayerUtils.wakka;

@NoArgsConstructor
@Environments(Env.PROD)
public class MobHeads extends Feature implements Listener {
	@Getter
	private static final Map<EntityType, ItemStack> mobHeads = new HashMap<>();
	@Getter
	private static final Map<EntityType, Double> mobChance = new HashMap<>();

	@Override
	public void onStart() {
		World world = Bukkit.getWorld("survival");
		WorldGuardUtils WGUtils = new WorldGuardUtils(world);
		WorldEditUtils WEUtils = new WorldEditUtils(world);

		for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads"))) {
			if (!MaterialTag.SIGNS.isTagged(block.getType()))
				continue;

			Sign sign = (Sign) block.getState();
			Directional directional = (Directional) sign.getBlockData();
			ItemStack skull = block.getRelative(directional.getFacing().getOppositeFace()).getRelative(BlockFace.UP)
					.getDrops().stream().findFirst().orElse(null);
			if (skull == null)
				continue;

			EntityType type;
			String entity = (sign.getLine(0) + sign.getLine(1)).trim();
			try {
				type = EntityType.valueOf(entity);
			} catch (Exception ignored) {
				Nexus.log("Cannot parse entity type: " + entity);
				continue;
			}

			double chance = Double.parseDouble(sign.getLine(3));

			skull = new ItemBuilder(skull).name("&e" + StringUtils.camelCase(type) + " Head").build();
			mobHeads.put(type, skull);
			mobChance.put(type, chance);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onKillEntity(EntityDeathEvent event) {
		if (wakka() != null && wakka().equals(event.getEntity().getKiller())) {
			wakka("-=- Entity Death Event -=-");
			wakka("Is Cancelled: " + event.isCancelled());
			wakka("");
			wakka("Type: " + event.getEntityType().name());
			LivingEntity entity = event.getEntity();
			wakka("Killer: " + entity.getKiller());
			if (entity.getLastDamageCause() != null)
				wakka("Last Damage Cause: " + entity.getLastDamageCause().getCause());
			wakka("Spawn Reason: " + entity.getEntitySpawnReason());
			wakka("Custom Name: " + entity.getCustomName());
			wakka("Will Despawn: " + entity.getRemoveWhenFarAway());
			wakka(" -=- ");
		}

		if (event.isCancelled())
			return;

		LivingEntity victim = event.getEntity();
		Player killer = victim.getKiller();
		if (killer == null)
			return;

		if (!new CooldownService().check(victim.getUniqueId(), "mobHead_entityId_death", Time.SECOND.x(2)))
			return;

		// TODO: Remove when done
		if (!PlayerUtils.isWakka(killer))
			return;
		//
		if (WorldGroup.get(killer) != WorldGroup.SURVIVAL)
			return;

		EntityType type = victim.getType();
		ItemStack skull = mobHeads.get(type);

		if (victim instanceof Player)
			skull = new ItemBuilder(skull).name("&e" + ((Player) victim).getDisplayName() + "'s Head").skullOwner((OfflinePlayer) victim).build();

		if (skull != null && RandomUtils.chanceOf(mobChance.get(type)))
			killer.getWorld().dropItemNaturally(victim.getLocation(), skull);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickupPlayerSkull(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		// TODO: Remove when done
		if (!PlayerUtils.isWakka(player))
			return;
		//

		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		if (!MaterialTag.SKULLS.isTagged(itemStack.getType()))
			return;

		UUID skullOwner = ItemUtils.getSkullOwner(itemStack);
		if (skullOwner != null) {
			for (ItemStack mobHead : mobHeads.values()) {
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

			ItemStack skull = mobHeads.values()
					.stream()
					.filter(mobHead -> mobHead.getType().equals(itemType))
					.collect(Collectors.toList())
					.get(0);
			item.setItemStack(skull);
			item.getItemStack().setAmount(itemStack.getAmount());
		}
	}

}
