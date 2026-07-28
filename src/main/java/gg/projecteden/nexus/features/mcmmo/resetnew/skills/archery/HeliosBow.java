package gg.projecteden.nexus.features.mcmmo.resetnew.skills.archery;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class HeliosBow implements Listener {

	private static final NamespacedKey ARROW_KEY = NamespacedKey.minecraft("helios_bow");

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.BOW)
		.name("&6Helios Bow")
		.model("survival/mcmmoreset/skills/archery/helios_bow")
		.build();

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		ItemStack bow = event.getBow();
		if (!ItemUtils.isModelMatch(bow, item)) return;

		if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
		PersistentDataContainer pdc = arrow.getPersistentDataContainer();
		pdc.set(ARROW_KEY, PersistentDataType.BOOLEAN, true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player player)) return;
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
		if (!arrow.getPersistentDataContainer().has(ARROW_KEY)) return;

		event.setCancelled(true);
		arrow.remove();

		if (event.getHitBlock() == null) return;
		if (event.getHitBlockFace() == null) return;

		if (event.getHitBlockFace() == BlockFace.DOWN) return;
		if (!event.getHitBlock().getBlockData().isFaceSturdy(event.getHitBlockFace(), BlockSupport.FULL)) return;

		Block blockToPlaceAt = event.getHitBlock().getRelative(event.getHitBlockFace());
		if (blockToPlaceAt.getType() != Material.AIR && !MaterialTag.REPLACEABLE_FIXED.isTagged(blockToPlaceAt.getType())) return;

		BlockData data = Material.TORCH.createBlockData();
		if (event.getHitBlockFace() != BlockFace.UP) {
			data = Material.WALL_TORCH.createBlockData();
			((Directional) data).setFacing(event.getHitBlockFace());
		}

		BlockUtils.tryPlaceEvent(player, blockToPlaceAt, event.getHitBlock(), Material.TORCH, data);
	}

	static {
		Nexus.registerListener(new HeliosBow());
	}

}
