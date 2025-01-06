package gg.projecteden.nexus.features.wither.fights;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MediumFight extends WitherFight {
	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.MEDIUM;
	}

	@Override
	public void spawnWither(Location location) {
		Wither wither = location.getWorld().spawn(location, Wither.class, SpawnReason.NATURAL);
		this.wither = wither;
		EntityUtils.setHealth(wither, wither.getHealth() * 2);
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (!RandomUtils.chanceOf(15))
			return;

		EnumUtils.random(CounterAttack.class).execute(alivePlayers());
	}

	@Override
	public boolean shouldGiveStar() {
		return RandomUtils.chanceOf(25);
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			add(CrateType.WITHER.getKey());
		}};
	}
}
