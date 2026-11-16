package gg.projecteden.nexus.features.mcmmo.mastery.skills.mining;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

public class BeaconEnhancer extends FloorThing implements TickableDecoration, Listener {

	private static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "enhanced");

	public BeaconEnhancer() {
		super(false, "Beacon Enhancer", ItemModelType.BEACON_ENHANCER);
		Nexus.registerListener(this);
	}

	@Override
	public void tick(Location location, ItemFrame itemFrame) {
		Block block = itemFrame.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (block.getType() != Material.BEACON) return;
		Beacon beacon = (Beacon) block.getState();
		set(beacon);
	}

	@EventHandler
	public void onPlace(DecorationPrePlaceEvent event) {
		if (!event.getDecoration().is(this)) return;
		if (event.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON)
			event.setCancelled(true);
	}

	@EventHandler
	public void onBreak(DecorationDestroyEvent event) {
		Block block = event.getDecoration().getItemFrame().getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (block.getType() != Material.BEACON) return;
		Beacon beacon = (Beacon) block.getState();
		reset(beacon);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBeaconEffect(BeaconEffectEvent event) {
		if (!(event.getBlock().getState() instanceof Beacon beacon)) return;
		if (!beacon.getPersistentDataContainer().has(KEY, PersistentDataType.BYTE)) return;

		PotionEffect effect = event.getEffect();
		event.setEffect(new PotionEffect(
			effect.getType(),
			effect.getDuration(),
			effect.getAmplifier() + 1,
			effect.isAmbient(),
			false,
			effect.hasIcon()
		));
	}

	private void set(Beacon beacon) {
		int tier = beacon.getTier();
		double range = 2.0 * (10 + 10 * tier);
		var data = beacon.getPersistentDataContainer();

		if (data.has(KEY, PersistentDataType.BYTE)
			&& Double.compare(beacon.getEffectRange(), range) == 0)
			return;

		data.set(KEY, PersistentDataType.BYTE, (byte) 1);
		beacon.setEffectRange(range);
		beacon.update(false, false);
	}

	private void reset(Beacon beacon) {
		var data = beacon.getPersistentDataContainer();
		if (!data.has(KEY, PersistentDataType.BYTE))
			return;

		data.remove(KEY);
		beacon.resetEffectRange();
		beacon.update(false, false);
	}

}
