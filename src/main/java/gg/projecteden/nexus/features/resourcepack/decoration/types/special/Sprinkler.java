package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.MoistureChangeEvent;

import java.util.Collection;

public class Sprinkler extends FloorThing implements TickableDecoration, Listener {

	public Sprinkler(String name, ItemModelType itemModelType) {
		super(false, name, itemModelType);
		Nexus.registerListener(this);
	}

	@Override
	public void tick(Location location, ItemFrame itemFrame) {
		for (int x = -10; x <= 10; x++) {
			for (int z = -10; z <= 10; z++) {
				// Hydrate soil
				if (RandomUtils.chanceOf(.3)) {
					Location loc = location.clone().toCenterLocation().add(x, -1, z);
					if (loc.getBlock().getBlockData() instanceof Farmland farmland) {
						farmland.setMoisture(7);
						loc.getBlock().setBlockData(farmland);
					}
				}

				// Increase rate
				if (RandomUtils.chanceOf(.15)) {
					Location loc = location.clone().toCenterLocation().add(x, 0, z);
					if (loc.getBlock().getBlockData() instanceof Ageable ageable) {
						if (ageable.getAge() == ageable.getMaximumAge()) continue;
						ageable.setAge(ageable.getAge() + 1);
						loc.getBlock().setBlockData(ageable);

						new ParticleBuilder(Particle.HAPPY_VILLAGER)
							.location(loc.toCenterLocation())
							.offset(.5, .5, .5)
							.allPlayers()
							.count(5)
							.spawn();
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onMoistureChange(MoistureChangeEvent event) {
		if (event.getBlock().getBlockData() instanceof Farmland current
			&& event.getNewState().getBlockData() instanceof Farmland next
			&& next.getMoisture() < current.getMoisture()) {

			Collection<ItemFrame> itemFrames = event.getBlock().getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 10);
			for (ItemFrame frame : itemFrames) {
				if (frame.getLocation().getBlock().getY() != event.getBlock().getY() + 1) continue;
				if (DecorationConfig.of(frame) instanceof Sprinkler) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onFarmlandFade(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.FARMLAND && event.getNewState().getType() == Material.DIRT) {

			Collection<ItemFrame> itemFrames = event.getBlock().getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, 10);
			for (ItemFrame frame : itemFrames) {
				if (frame.getLocation().getBlock().getY() != event.getBlock().getY() + 1) continue;
				if (DecorationConfig.of(frame) instanceof Sprinkler) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
