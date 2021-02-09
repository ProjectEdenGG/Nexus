package me.pugabyte.nexus.features.quests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
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

			mobHeads.put(type, skull);
			mobChance.put(type, chance);
		}
	}

	@EventHandler
	public static void onKillEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity))
			return;

		if (!(event.getDamager() instanceof Player))
			return;

		LivingEntity victim = (LivingEntity) event.getEntity();
		double newHealth = victim.getHealth() - event.getFinalDamage();
		if (newHealth > 0)
			return;

		Player killer = (Player) event.getDamager();
		// TODO: Remove when done
		if (!PlayerUtils.isWakka(killer))
			return;
		//

		EntityType type = victim.getType();
		ItemStack skull;

		if (victim instanceof Player)
			skull = new ItemBuilder(Material.PLAYER_HEAD).skullOwner((OfflinePlayer) victim).build();
		else
			skull = new ItemBuilder(mobHeads.get(type)).name("&e" + StringUtils.camelCase(type) + " Head").build();

		if (skull != null && RandomUtils.chanceOf(mobChance.get(type)))
			ItemUtils.giveItem(killer, skull);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreakPlayerSkull(BlockBreakEvent event) {
		// TODO: Remove when done
		if (!PlayerUtils.isWakka(event.getPlayer()))
			return;
		//

		Block block = event.getBlock();
		if (!MaterialTag.SKULLS.isTagged(event.getBlock().getType()))
			return;

		Skull skull = (Skull) block.getState();
		if (skull.getOwningPlayer() == null)
			return;
		UUID skullOwner = skull.getOwningPlayer().getUniqueId();

		for (ItemStack mobhead : mobHeads.values()) {
			if (!MaterialTag.SKULLS.isTagged(mobhead.getType()))
				continue;

			UUID mobOwner = ItemUtils.getSkullOwner(mobhead);
			if (mobOwner == null)
				continue;

			// TODO: it's not dropping the correct skull
			if (mobOwner.equals(skullOwner)) {
				event.setDropItems(false);
				event.getBlock().getDrops().clear();
				block.getWorld().dropItemNaturally(block.getLocation(), mobhead.clone());
				PlayerUtils.wakka("dropping mob head instead");
				break;
			}

		}

	}


}
