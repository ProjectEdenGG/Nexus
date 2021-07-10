package me.pugabyte.nexus.features.wither.fights;

import eden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.features.wither.models.WitherFight;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
		AttributeInstance health = wither.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		health.setBaseValue(health.getValue() * 2);
		wither.setHealth(health.getBaseValue());
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither) return;
		if (!RandomUtils.chanceOf(15)) return;
		EnumUtils.random(CounterAttack.class).execute(alivePlayers);
	}

	@Override
	public boolean shouldGiveStar() {
		return (Math.random() * 101) < 25;
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			ItemStack key = CrateType.BOSS.getKey();
			key.setAmount(2);
			add(key);
		}};
	}
}
