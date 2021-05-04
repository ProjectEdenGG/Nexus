package me.pugabyte.nexus.features.crates.crates;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.crates.models.events.CrateSpawnItemEvent;
import me.pugabyte.nexus.features.crates.models.exceptions.CrateOpeningException;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FebVoteRewardCrate extends Crate {
	@Override
	public CrateType getCrateType() {
		return CrateType.FEB_VOTE_REWARD;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&c&l--=[+]=--");
			add("&c[+] &6&lFeb. Vote Goal Crate &c[+]");
			add("&c&l--=[+]=--");
		}};
	}

	@Override
	public Particle getParticleType() {
		return Particle.FLAME;
	}

	@Override
	public void playFinalParticle(Location location) {
		for (int i = 0; i < 5; i++)
			new ParticleBuilder(Particle.LAVA)
					.count(25)
					.location(location)
					.spawn();
	}

	@Override
	public Item spawnItem(Location location, ItemStack itemStack) {
		try {
			Item item = location.getWorld().spawn(location, Item.class);
			item.setVelocity(new Vector(0, 0, 0));
			item.setItemStack(itemStack);
			item.setCanPlayerPickup(false);
			item.setCustomName(itemStack.getItemMeta().getDisplayName());
			item.setCustomNameVisible(true);
			spawnedItem = item;
			new CrateSpawnItemEvent(player, loot, getCrateType()).callEvent();
			return item;
		} catch (Exception ex) {
			player.getInventory().addItem(getCrateType().getKey());
			throw new CrateOpeningException("There was an error while trying to play the crate animation");
		}
	}

}
