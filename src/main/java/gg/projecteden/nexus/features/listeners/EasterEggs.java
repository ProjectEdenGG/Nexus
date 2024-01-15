package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EasterEggs implements Listener {

	@Data
	@NoArgsConstructor
	public static class StaffEasterEggBuilder {
		private @NonNull UUID uuid;
		private @NonNull Set<Material> food = new HashSet<>();
		private boolean consumeFood = true;

		private @Nullable SoundBuilder eatSound = new SoundBuilder(Sound.ENTITY_GENERIC_EAT).volume(0.5);
		private int eatSoundCount = 5;
		private @NonNull BiConsumer<Player, ItemStack> eatEffect = (player, itemstack) -> {
			Location headLoc = player.getLocation().add(0, 1.45, 0);
			Location mouthLoc = headLoc.add(player.getEyeLocation().getDirection().multiply(0.25));
			new ParticleBuilder(Particle.ITEM_CRACK).data(itemstack).location(mouthLoc).count(15).extra(0.1).spawn();
		};
		private int eatEffectCount = 5;
		private int eatMaxCount = 5;

		private @Nullable SoundBuilder burpSound = new SoundBuilder(Sound.ENTITY_PLAYER_BURP).volume(0.5);
		private @Nullable BiConsumer<Player, ItemStack> burpEffect;

		private @NonNull Set<Material> rejectFood = new HashSet<>();
		private @Nullable BiConsumer<Player, ItemStack> rejectEffect;

		public StaffEasterEggBuilder(String uuid) {
			this.uuid = UUID.fromString(uuid);
		}

		public StaffEasterEggBuilder food(Material food) {
			this.food = Collections.singleton(food);
			return this;
		}

		public StaffEasterEggBuilder food(Set<Material> food) {
			this.food = food;
			return this;
		}

		public StaffEasterEggBuilder consumeFood(boolean consumeFood) {
			this.consumeFood = consumeFood;
			return this;
		}

		public StaffEasterEggBuilder burpSound(Sound burpSound) {
			this.burpSound = new SoundBuilder(burpSound);
			return this;
		}

		public StaffEasterEggBuilder burpSound(SoundBuilder burpSound) {
			this.burpSound = burpSound;
			return this;
		}

		public StaffEasterEggBuilder noBurpSound() {
			this.burpSound = null;
			return this;
		}

		public StaffEasterEggBuilder burpEffect(BiConsumer<Player, ItemStack> effect) {
			this.burpEffect = effect;
			return this;
		}

		public StaffEasterEggBuilder eatSound(Sound eatSound) {
			this.eatSound = new SoundBuilder(eatSound);
			return this;
		}

		public StaffEasterEggBuilder eatSound(SoundBuilder eatSound) {
			this.eatSound = eatSound;
			return this;
		}

		public StaffEasterEggBuilder noEatSound() {
			this.eatSound = null;
			return this;
		}

		public StaffEasterEggBuilder eatSoundCount(int count) {
			this.eatSoundCount = count;
			return this;
		}

		public StaffEasterEggBuilder eatEffect(BiConsumer<Player, ItemStack> effect) {
			this.burpEffect = effect;
			return this;
		}

		public StaffEasterEggBuilder eatEffectCount(int count) {
			this.eatEffectCount = count;
			return this;
		}

		public StaffEasterEggBuilder eatMaxCount(int count) {
			this.eatMaxCount = count;
			return this;
		}

		public StaffEasterEggBuilder rejectFood(Material rejectFood) {
			this.rejectFood = Collections.singleton(rejectFood);
			return this;
		}

		public StaffEasterEggBuilder rejectFood(Set<Material> rejectFood) {
			this.rejectFood = rejectFood;
			return this;
		}

		public StaffEasterEggBuilder rejectEffect(BiConsumer<Player, ItemStack> effect) {
			this.rejectEffect = effect;
			return this;
		}

		public void consume(ItemStack itemStack, Player clicker) {
			if (!food.contains(itemStack.getType()))
				return;

			Player clicked = PlayerUtils.getPlayer(uuid).getPlayer();
			if (clicked == null)
				return;

			if (!new CooldownService().check(uuid, "Staff_EasterEgg_" + clicked.getName(), TickTime.SECOND.x(2)))
				return;

			ItemStack foodItem = itemStack.clone();
			boolean isReject = rejectFood.contains(foodItem.getType());
			if (consumeFood) {
				itemStack.subtract();
				if (!isReject)
					clicked.setFoodLevel(clicked.getFoodLevel() + 2);
			}

			int wait = 0;
			for (int i = 0; i < eatMaxCount; i++) {
				int finalI = i;
				Tasks.wait(wait, () -> {
					if (eatSound != null) {
						if (finalI < eatSoundCount)
							eatSound.clone().location(clicked.getLocation()).play();
					}

					if (finalI < eatEffectCount)
						eatEffect.accept(clicked, foodItem);
				});

				wait += 4;
			}

			wait += 4;

			Tasks.wait(wait, () -> {
				if (isReject) {
					if (rejectEffect != null)
						rejectEffect.accept(clicked, itemStack);
					return;
				}


				if (burpSound != null) {
					burpSound.clone().location(clicked.getLocation()).play();
					if (burpEffect != null)
						burpEffect.accept(clicked, itemStack);
				}
			});
		}

	}

	/*
	 	TODO: Filid, Bri, MaxAlex, Panda, JJ, Steve
	 */
	@AllArgsConstructor
	public enum StaffEasterEgg {
		// Admins
		GRIFFIN(new StaffEasterEggBuilder("86d7e0e2-c95e-4f22-8f99-a6e83b398307")
			.food(Material.ICE)
		),

		WAKKA(new StaffEasterEggBuilder("e9e07315-d32c-4df7-bd05-acfe51108234")
			.food(Material.REDSTONE)
			.burpSound(new SoundBuilder("custom.crates.weeklywakka.burp").volume(0.5))
		),

		BLAST(new StaffEasterEggBuilder("a4274d94-10f2-4663-af3b-a842c7ec729c")
			.food(Material.TNT)
			.eatSound(Sound.ENTITY_TNT_PRIMED)
			.eatSoundCount(1)
			.burpSound(Sound.ENTITY_GENERIC_EXPLODE)
			.burpEffect((player, itemStack) -> {
				new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(player).play();

				new ParticleBuilder(Particle.EXPLOSION_HUGE)
					.count(10)
					.offset(.5, .5, .5)
					.location(player.getLocation())
					.spawn();
			})
		),

		// Operators

		RAVEN(new StaffEasterEggBuilder("fce1fe67-9514-4117-bcf6-d0c49ca0ba41")
			.food(MaterialTag.SEEDS.getValues())
			.eatSound(Sound.ENTITY_PARROT_EAT)
			.burpSound(Sound.ENTITY_PARROT_AMBIENT)
		),

		JOSH(new StaffEasterEggBuilder("5c3ddda9-51e1-4f9a-8233-e08cf0b26f11")
			.food(Material.MYCELIUM)
			.burpSound(Sound.ENTITY_COW_AMBIENT)
		),

		CYN(new StaffEasterEggBuilder("1d70383f-21ba-4b8b-a0b4-6c327fbdade1")
			.food(Material.GOLD_NUGGET)
			.burpSound(Sound.ENTITY_PIGLIN_ADMIRING_ITEM)
		),

		// Moderators

		BOFFO(new StaffEasterEggBuilder("b83bae78-83d6-43a0-9316-014a0a702ab2")
			.food(Set.of(Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.GRANITE, Material.DIORITE))
			.burpSound(Sound.ENTITY_VILLAGER_YES)
		),

		KNACK(new StaffEasterEggBuilder("32fc75e3-a278-43c4-99a7-90af03846dad")
			.food(Material.EGG)
			.burpSound(Sound.ENTITY_CHICKEN_AMBIENT)
		),

		MARSHY(new StaffEasterEggBuilder("a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3")
			.food(Material.STONE_BRICKS)
			.burpSound(Sound.ENTITY_WARDEN_AMBIENT)
		),

		// Architects

		THUNDER(new StaffEasterEggBuilder("6dbb7b77-a68e-448d-a5bc-fb531a7fe22d")
			.food(Material.LIGHTNING_ROD)
			.eatSound(new SoundBuilder(Sound.BLOCK_COPPER_STEP).volume(1))
			.burpSound(new SoundBuilder(Sound.ENTITY_LIGHTNING_BOLT_THUNDER).volume(0.5))
		),

		POWER(new StaffEasterEggBuilder("79f66fc9-a975-4043-8b6d-b4823182de62")
			.food(MaterialTag.FLOWERS.getValues())
			.rejectFood(Set.of(Material.WITHER_ROSE, Material.TORCHFLOWER))
			.rejectEffect((player, itemStack) -> {
				double health = player.getHealth();
				switch (itemStack.getType()) {
					case WITHER_ROSE -> {
						player.damage(1);
						Tasks.wait(TickTime.TICK.x(5), () -> player.setHealth(health));
					}
					case TORCHFLOWER -> {
						player.setFireTicks((int) TickTime.SECOND.x(2));
						Tasks.wait(TickTime.SECOND.x(2.5), () -> player.setHealth(health));
					}
				}
			})
		),

		BRI(new StaffEasterEggBuilder("77966ca3-ac85-44b2-bcb0-b7c5f9342e86")
			.food(Material.SWEET_BERRIES)
			.eatSound(Sound.ENTITY_AXOLOTL_IDLE_AIR)
			.eatSoundCount(2)
			.burpSound(Sound.ENTITY_AXOLOTL_IDLE_WATER)
			.burpEffect(((player, itemStack) -> {
				new ParticleBuilder(Particle.GLOW)
					.count(25)
					.offset(.35, .5, .35)
					.location(player.getLocation().add(0, 1, 0))
					.spawn();
			}))
		),

		// Builders

		HOOTS(new StaffEasterEggBuilder("4f06f692-0b42-4706-9193-bcc716ce5936")
			.food(Material.AMETHYST_SHARD)
			.eatSound(Sound.BLOCK_AMETHYST_CLUSTER_PLACE)
			.burpSound(Sound.BLOCK_AMETHYST_CLUSTER_BREAK)
		),
		;

		private final StaffEasterEggBuilder builder;

		public static @Nullable StaffEasterEgg of(Player player) {
			for (StaffEasterEgg easterEgg : values()) {
				if (easterEgg.builder.uuid.equals(player.getUniqueId()))
					return easterEgg;
			}
			return null;
		}
	}

	@EventHandler
	public void onClickOnPlayer(PlayerInteractEntityEvent event) {
		if (WorldGroup.of(event.getPlayer()) == WorldGroup.MINIGAMES)
			return;

		if (!event.getRightClicked().getType().equals(EntityType.PLAYER))
			return;

		if (CitizensUtils.isNPC(event.getRightClicked()))
			return;

		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;

		Player clicked = (Player) event.getRightClicked();
		Player clicker = event.getPlayer();
		ItemStack heldItem = clicker.getInventory().getItemInMainHand();
		if (Nullables.isNullOrAir(heldItem))
			return;

		StaffEasterEgg staffEasterEgg = StaffEasterEgg.of(clicked);
		if (staffEasterEgg == null)
			return;

		event.setCancelled(true);
		staffEasterEgg.builder.consume(heldItem, clicker);
	}
}
