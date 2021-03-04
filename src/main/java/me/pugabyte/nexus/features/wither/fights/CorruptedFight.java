package me.pugabyte.nexus.features.wither.fights;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.features.wither.models.WitherFight;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public class CorruptedFight extends WitherFight {

	public double maxHealth;
	public boolean shouldSummonFirstWave = true;
	public boolean shouldSummonSecondWave = true;

	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.CORRUPTED;
	}

	@Override
	public void spawnWither(Location location) {
		Wither wither = location.getWorld().spawn(location, Wither.class);
		this.wither = wither;
		AttributeInstance health = wither.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		health.setBaseValue(health.getValue() * 3);
		wither.setHealth(health.getBaseValue());
		maxHealth = health.getBaseValue();
	}

	@Override
	public boolean shouldGiveStar() {
		return true;
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return null;
	}

	@EventHandler
	public void onWitherRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() != wither) return;
		if (shouldRegen) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither) return;
		if (!RandomUtils.chanceOf(25)) return;
		EnumUtils.random(CounterAttack.class).execute(alivePlayers);
	}

	@EventHandler
	public void onDamageWither(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither) return;
		Wither wither = (Wither) event.getEntity();
		if (!shouldRegen)
			broadcastToParty("&cThe wither cannot be damaged while the blaze shield is up! &eKill the blazes to continue the fight!");
		if (!shouldSummonFirstWave && !shouldSummonSecondWave) return;
		if (wither.getHealth() - event.getFinalDamage() < (maxHealth / 3) * 2 && shouldSummonFirstWave) {
			shouldSummonFirstWave = false;
			shouldRegen = false;
			spawnPiglins(15);
			spawnBrutes(2);
			spawnHoglins(2);
			wither.setAI(false);
			wither.setGravity(false);
			wither.setInvulnerable(true);
			wither.teleport(WitherChallenge.cageLoc);
			this.blazes = spawnBlazes(15, 8);
		} else if (wither.getHealth() - event.getFinalDamage() < maxHealth / 3 && shouldSummonSecondWave) {
			shouldSummonSecondWave = false;
			shouldRegen = false;
			spawnPiglins(20);
			spawnBrutes(2);
			spawnHoglins(2);
			wither.setAI(false);
			wither.setGravity(false);
			wither.setInvulnerable(true);
			wither.teleport(WitherChallenge.cageLoc);
			this.blazes = spawnBlazes(10, 6);
			this.blazes.addAll(spawnBlazes(10, 9));
		}
	}

}
