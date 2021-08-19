package gg.projecteden.nexus.features.wither.fights;

import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.utils.RandomUtils.chanceOf;

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
		maxHealth = EntityUtils.setHealth(wither, wither.getHealth() * 2);
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (event.getEntity() != this.wither)
			return;

		if (!chanceOf(10))
			return;

		EnumUtils.random(CounterAttack.class).execute(alivePlayers());
	}

	@Override
	public boolean shouldGiveStar() {
		return chanceOf(50);
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
		if (event.getEntity() != this.wither)
			return;

		Wither wither = (Wither) event.getEntity();
		if (!shouldRegen)
			if (event.getDamager() instanceof Player player)
				PlayerUtils.send(player, WitherChallenge.PREFIX + "&cThe wither cannot be damaged while the blaze shield is up! " +
					"&eKill the blazes to continue the fight!");

		if (!shouldSummonWave)
			return;

		if (wither.getHealth() - event.getFinalDamage() > maxHealth / 2)
			return;

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
