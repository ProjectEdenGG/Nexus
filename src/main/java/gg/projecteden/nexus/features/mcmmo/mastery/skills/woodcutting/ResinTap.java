package gg.projecteden.nexus.features.mcmmo.mastery.skills.woodcutting;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ResinTap extends WallThing implements TickableDecoration, Listener {

	private static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "resin_tap");

	public ResinTap() {
		super(false, "Resin Tap", ItemModelType.RESIN_TAP);
		Nexus.registerListener(this);
	}

	@Override
	public void tick(Location location, ItemFrame itemFrame) {
		if (location.getBlock().getRelative(itemFrame.getAttachedFace()).getType() != Material.PALE_OAK_LOG)
			return;

		PersistentDataContainer pdc = itemFrame.getPersistentDataContainer();
		if (!pdc.has(KEY, PersistentDataType.INTEGER)) {
			pdc.set(KEY, PersistentDataType.INTEGER, 0);
		}
		else
			pdc.set(KEY, PersistentDataType.INTEGER, pdc.get(KEY, PersistentDataType.INTEGER) + 1);

		if (pdc.get(KEY, PersistentDataType.INTEGER) <= 20)
			return;

		pdc.set(KEY, PersistentDataType.INTEGER, 0);

		if (RandomUtils.chanceOf(50))
			return;

		Location loc = itemFrame.getLocation().toCenterLocation();
		loc.getWorld().dropItem(loc, new ItemStack(Material.RESIN_CLUMP));
	}

	@EventHandler
	public void onPlace(DecorationPrePlaceEvent event) {
		if (!event.getDecoration().is(this)) return;
		if (event.getLocation().getBlock().getRelative(event.getAttachedFace().getOppositeFace()).getType() != Material.PALE_OAK_LOG)
			event.setCancelled(true);
	}

}
