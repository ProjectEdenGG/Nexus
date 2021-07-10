package me.pugabyte.nexus.features.wither.fights;

import eden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.features.wither.models.WitherFight;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class HardFight extends WitherFight {

	public double maxHealth;
	public boolean shouldSummonWave = true;

	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.HARD;
	}

	@Override
	public void spawnWither(Location location) {
		Wither wither = location.getWorld().spawn(location, Wither.class, SpawnReason.NATURAL);
		this.wither = wither;
		AttributeInstance health = wither.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		health.setBaseValue(health.getValue() * 2);
		wither.setHealth(health.getBaseValue());
		maxHealth = health.getBaseValue();
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither) return;
		if (!RandomUtils.chanceOf(10)) return;
		EnumUtils.random(CounterAttack.class).execute(alivePlayers);
	}

	@Override
	public boolean shouldGiveStar() {
		return Math.random() > .5;
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			ItemStack key = CrateType.BOSS.getKey();
			key.setAmount(3);
			add(key);
		}};
	}

	@EventHandler
	public void onDamageWither(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither) return;
		Wither wither = (Wither) event.getEntity();
		if (!shouldRegen) {
			if (event.getDamager() instanceof Player player) {
				PlayerUtils.send(player, WitherChallenge.PREFIX + "&cThe wither cannot be damaged while the blaze shield is up! &eKill the blazes to continue the fight!");
			}
		}
		if (!shouldSummonWave) return;
		if (wither.getHealth() - event.getFinalDamage() > maxHealth / 2) return;
		shouldSummonWave = false;
		shouldRegen = false;
		spawnHoglins(1);
		spawnBrutes(2);
		spawnPiglins(10);

		wither.setAI(false);
		wither.setGravity(false);
		wither.setInvulnerable(true);
		wither.teleport(WitherChallenge.cageLoc);
		this.blazes = spawnBlazes(10, 8);
	}


}
