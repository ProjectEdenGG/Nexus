package gg.projecteden.nexus.features.wither.fights;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class HardFight extends WitherFight {

	public double maxHealth;
	public boolean shouldSummonWave = true;
	public boolean goingToCenter = false;

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
		if (!event.getEntity().equals(wither))
			return;

		if (!RandomUtils.chanceOf(10))
			return;

		EnumUtils.random(CounterAttack.class).execute(alivePlayers());
	}

	@Override
	public boolean shouldGiveStar() {
		return RandomUtils.chanceOf(50);
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			ItemStack key = CrateType.WITHER.getKey();
			key.setAmount(2);
			add(key);
		}};
	}

	@EventHandler
	public void onDamageWither(EntityDamageByEntityEvent event) {
		if (!event.getEntity().equals(wither))
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

		this.goToCenter();
	}

	public void goToCenter() {
		goingToCenter = true;
		wither.getPathfinder().moveTo(WitherChallenge.cageLoc);
		AtomicInteger taskId = new AtomicInteger();
		taskId.set(Tasks.repeat(1 ,1, () -> {
			if (!wither.getPathfinder().hasPath()) {
				arriveAtCenter(1, 2, 10);
				Tasks.cancel(taskId.get());
			}
		}));
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (goingToCenter)
			event.setCancelled(true);
	}

	public void arriveAtCenter(int hoglins, int brutes, int piglins) {
		goingToCenter = false;
		shouldSummonWave = false;
		shouldRegen = false;
		spawnHoglins(hoglins);
		spawnBrutes(brutes);
		spawnPiglins(piglins);

		wither.setAI(false);
		wither.setGravity(false);
		wither.setInvulnerable(true);
		wither.teleport(WitherChallenge.cageLoc);
		this.blazes = spawnBlazes(10, 8);
	}

}
