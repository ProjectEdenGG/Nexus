package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Pugmas24Fishing implements Listener {

	public Pugmas24Fishing() {
		Nexus.registerListener(this);

		lavaFishing();
	}

	@EventHandler
	public void onFishBite(PlayerEventFishingBiteEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas24.get().shouldHandle(player))
			return;

		PlayerUtils.send(player, "Loot:");
		for (ItemStack itemStack : event.getLoot()) {
			new JsonBuilder(" - " + itemStack.getType()).hover(itemStack).send(player);
		}
	}

	private static final Map<Player, HookData> hookMap = new ConcurrentHashMap<>();

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.getState() != State.FISHING)
			return;

		Player player = event.getPlayer();
		if (!Pugmas24.get().shouldHandle(player))
			return;

		hookMap.put(player, new HookData(event.getHook()));
	}

	/*
		TODO: CATCHING LOOT
	 */
	public void lavaFishing() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			for (Player player : hookMap.keySet()) {
				HookData data = hookMap.getOrDefault(player, null);

				if (data == null || !data.canStart())
					continue;

				if (data.shouldStop()) {
					data.destroy();
					hookMap.remove(player);
					continue;
				}


				float lavaHeight = 0F;
				FishHook hook = data.getHook();
				Block block = hook.getLocation().getBlock();
				if (block.getType() == Material.LAVA) {
					if (block.getBlockData() instanceof Levelled levelled) {
						lavaHeight = (float) (levelled.getLevel() * 0.125);
					}
				}

				if (data.getNibble() > 0) {
					data.nibble();
					if (hook.getY() % 1 <= lavaHeight) {
						data.incJumpTimer();
						if (data.getJumpTimer() >= 4) {
							data.setJumpTimer(0);
							hook.setVelocity(new Vector(0, 0.24, 0));
						}
					}

					if (data.getNibble() <= 0) {
						data.setTimeUntilLured(0);
						data.setTimeUntilHooked(0);
						data.setHooked(false);
						data.setJumpTimer(0);
						data.setCurrentState(0);
					}
				} else {
					if (hook.getY() % 1 <= lavaHeight || hook.isInLava()) {
						Vector previousVector = hook.getVelocity();
						double x = previousVector.getX() * 0.6;
						double y = Math.min(0.1, Math.max(-0.1, previousVector.getY() + 0.1));
						double z = previousVector.getZ() * 0.6;
						hook.setVelocity(new Vector(x, y, z));
						data.setCurrentState(1);
					} else {
						if (data.getCurrentState() == 1) {
							data.setCurrentState(0);
							// set temp entity
							Location spawnLoc = hook.getLocation().clone().subtract(0, 1, 0);
							data.setTempEntity(hook.getWorld().spawn(spawnLoc, ArmorStand.class));
							data.setCaughtEntityProperties(data.getTempEntity());
							hook.setHookedEntity(data.getTempEntity());
							if (!data.isFirstTime()) {
								// HookStateEvent --> ESCAPE
							}
							data.setFirstTime(false);
						}
					}

					// float shit
					float f, f1, f2;
					double d0, d1, d2;
					if (data.getTimeUntilHooked() > 0) {
						data.decTimeUntilHooked();
						if (data.getTimeUntilHooked() > 0) {
							data.setFishAngle((float) (data.getFishAngle() + RandomUtils.triangle(0, 9.188)));
							f = data.getFishAngle() * 0.017453292F; // we love magic numbers
							f1 = (float) Math.sin(f);
							f2 = (float) Math.cos(f);
							d0 = hook.getX() + (f1 + data.getTimeUntilHooked() * 0.1);
							d1 = hook.getY();
							d2 = hook.getZ() + (f2 + data.getTimeUntilHooked() * 0.1);
							if (RandomUtils.randomDouble(0, 1) < 0.15) {
								hook.getWorld().spawnParticle(Particle.FLAME, d0, d1 - 0.10000000149011612D, d2, 1, f1, 0.1, f2, 0.0);
							}
							float f3 = f1 * 0.04f;
							float f4 = f2 * 0.04f;
							hook.getWorld().spawnParticle(Particle.FLAME, d0, d1, d2, 0, f4, 0.01, -f3, 1.0);
						} else {
							double d3 = hook.getY() + 0.5;
							hook.getWorld().spawnParticle(Particle.FLAME, hook.getX(), d3, hook.getZ(), (int) (1.0F + 0.3 * 20.0F), 0.3, 0.0D, 0.3, 0.20000000298023224D);
							data.setNibble(RandomUtils.randomInt(20, 40));
							data.setHooked(true);
							hook.getWorld().playSound(hook.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.25F, (float) (1.0F + (RandomUtils.randomDouble(0, 1) - RandomUtils.randomDouble(0, 1)) * 0.4F));
							// HookStateEvent --> BITE
							if (data.getTempEntity() != null && data.getTempEntity().isValid()) {
								data.getTempEntity().remove();
							}
						}
					} else if (data.getTimeUntilLured() > 0) {
						if (!data.isFreeze()) {
							data.decTimeUntilLured();
						}
						if (data.getTimeUntilLured() <= 0) {
							data.setFishAngle((float) RandomUtils.randomDouble(0, 360));
							data.setTimeUntilHooked(RandomUtils.randomInt(20, 80));
							// HookStateEvent --> LURE
						}
					} else {
						data.setWaitTime();
					}
				}
			}
		});
	}

	@Setter
	@Getter
	private static class HookData {
		FishHook hook;
		ArmorStand tempEntity;
		int timeUntilLured;
		int timeUntilHooked;
		int nibble;
		boolean hooked;
		float fishAngle;
		int currentState;
		int jumpTimer;
		boolean firstTime;
		boolean freeze;

		public HookData(FishHook hook) {
			this.hook = hook;
			setWaitTime();
		}

		public void nibble() {
			--this.nibble;
		}

		public void incJumpTimer() {
			this.jumpTimer++;
		}

		public void decTimeUntilHooked() {
			this.timeUntilHooked--;
		}

		public void decTimeUntilLured() {
			this.timeUntilLured--;
		}

		public void setWaitTime() {
			int lavaMinTime = 20;
			int lavaMaxTime = 80;
			int time = ThreadLocalRandom.current().nextInt(lavaMaxTime - lavaMinTime + 1) + lavaMinTime;
			Dev.WAKKA.send("Wait time: " + time + " ticks");
			this.timeUntilLured = time;
		}

		public boolean canStart() {
			if (hook.isInLava())
				return true;

			float lavaHeight = 0F;
			Block block = hook.getLocation().getBlock();
			if (block.getType() == Material.LAVA) {
				if (block.getBlockData() instanceof Levelled levelled) {
					lavaHeight = (float) (levelled.getLevel() * 0.125);
				}
			}

			return lavaHeight > 0 && hook.getY() % 1 <= lavaHeight;
		}


		public boolean shouldStop() {
			if (hook == null || !hook.isValid())
				return true;

			if (hook.isInLava())
				return false;

			Block block = hook.getLocation().getBlock();
			Block below = block.getRelative(BlockFace.DOWN);
			return hook.isOnGround() || (block.getType() != Material.LAVA && below.getType() != Material.LAVA);
		}

		public void destroy() {
			if (this.hook.isValid())
				hook.remove();

			this.hook = null;

			if (this.tempEntity != null && this.tempEntity.isValid()) {
				this.tempEntity.remove();
			}

			this.freeze = false;
		}

		private void setCaughtEntityProperties(ArmorStand entity) {
			entity.setInvisible(true);
			entity.setCollidable(false);
			entity.setInvulnerable(true);
			entity.setVisible(false);
			entity.setCustomNameVisible(false);
			entity.setSmall(true);
			entity.setGravity(false);
		}

		private enum HookState {
			BITE,
			ESCAPE,
			LURE,
			LAND,
			;
		}

		public void stateChange(HookState hookState) {
			switch (hookState) {
				case BITE -> onBite();
				case LAND -> onLand();
				case ESCAPE -> onEscape();
				case LURE -> onLure();
			}
		}

		private void onBite() {

		}

		private void onLand() {

		}

		private void onEscape() {

		}

		private void onLure() {

		}


	}



}
