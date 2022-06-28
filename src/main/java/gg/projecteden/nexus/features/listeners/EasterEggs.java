package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
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
		private @NonNull SoundBuilder eatSound = new SoundBuilder(Sound.ENTITY_GENERIC_EAT).volume(0.5);
		private @NonNull BiConsumer<Player, ItemStack> eatEffect = (player, itemstack) -> {
			Location headLoc = player.getLocation().add(0, 1.45, 0);
			Location mouthLoc = headLoc.add(player.getEyeLocation().getDirection().multiply(0.25));
			new ParticleBuilder(Particle.ITEM_CRACK).data(itemstack).location(mouthLoc).count(15).extra(0.1).spawn();
		};
		private @NonNull SoundBuilder burpSound = new SoundBuilder(Sound.ENTITY_PLAYER_BURP).volume(0.5);
		private @Nullable BiConsumer<Player, ItemStack> burpEffect;
		private boolean consumeFood = true;

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

		public StaffEasterEggBuilder burpEffect(BiConsumer<Player, ItemStack> burpEffect) {
			this.burpEffect = burpEffect;
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

		public StaffEasterEggBuilder eatEffect(BiConsumer<Player, ItemStack> eatEffect) {
			this.burpEffect = eatEffect;
			return this;
		}

		public void consume(ItemStack itemStack, Player clicker) {
			if (!this.food.contains(itemStack.getType()))
				return;

			Player clicked = PlayerUtils.getPlayer(this.uuid).getPlayer();
			if (clicked == null)
				return;

			if (!new CooldownService().check(uuid, "Staff_EasterEgg_" + clicked.getName(), TickTime.SECOND))
				return;

			ItemStack foodItem = itemStack.clone();
			if (consumeFood)
				itemStack.subtract();

			clicked.setFoodLevel(clicked.getFoodLevel() + 2);

			Location location = clicked.getLocation();
			SoundBuilder eatSound = this.getEatSound().clone().location(location);
			SoundBuilder burpSound = this.getBurpSound().clone().location(location);

			// Eat effect needs to appear near the mouth of the player clicked
			eatSound.play();
			eatEffect.accept(clicked, foodItem);

			Tasks.wait(4, () -> {
				eatSound.play();
				eatEffect.accept(clicked, foodItem);
			});
			Tasks.wait(8, () -> {
				eatSound.play();
				eatEffect.accept(clicked, foodItem);
			});
			Tasks.wait(12, () -> {
				eatSound.play();
				eatEffect.accept(clicked, foodItem);
			});
			Tasks.wait(16, () -> {
				eatSound.play();
				eatEffect.accept(clicked, foodItem);
			});

			Tasks.wait(20, () -> {
				burpSound.play();
				if (burpEffect != null) burpEffect.accept(clicked, itemStack);
			});
		}

	}

	@AllArgsConstructor
	public enum StaffEasterEgg {
		GRIFFIN(new StaffEasterEggBuilder("86d7e0e2-c95e-4f22-8f99-a6e83b398307")
			.food(Material.ICE)),

		WAKKA(new StaffEasterEggBuilder("e9e07315-d32c-4df7-bd05-acfe51108234")
			.food(Material.REDSTONE)),

		BLAST(new StaffEasterEggBuilder("a4274d94-10f2-4663-af3b-a842c7ec729c")
			.food(Material.TNT)
			.burpSound(Sound.ENTITY_GENERIC_EXPLODE)
			.burpEffect((player, itemStack) -> {
				new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(player).play();

				new ParticleBuilder(Particle.EXPLOSION_HUGE)
					.count(10)
					.offset(.5, .5, .5)
					.location(player.getLocation())
					.spawn();
			})),

		LEXI(new StaffEasterEggBuilder("d1de9ca8-78f6-4aae-87a1-8c112f675f12")
			.food(MaterialTag.CANDLES.getValues())
			.burpSound(new SoundBuilder(Sound.BLOCK_FIRE_AMBIENT).volume(2))),

		RAVEN(new StaffEasterEggBuilder("fce1fe67-9514-4117-bcf6-d0c49ca0ba41")
			.food(MaterialTag.SEEDS.getValues())
			.eatSound(Sound.ENTITY_PARROT_EAT)
			.burpSound(Sound.ENTITY_PARROT_AMBIENT)),

		THUNDER(new StaffEasterEggBuilder("6dbb7b77-a68e-448d-a5bc-fb531a7fe22d")
			.food(Material.LIGHTNING_ROD)
			.eatSound(new SoundBuilder(Sound.BLOCK_COPPER_STEP).volume(1))
			.burpSound(new SoundBuilder(Sound.ENTITY_LIGHTNING_BOLT_THUNDER).volume(0.5))),

		KNACK(new StaffEasterEggBuilder("32fc75e3-a278-43c4-99a7-90af03846dad")
			.food(Material.EGG)
			.burpSound(Sound.ENTITY_CHICKEN_AMBIENT)),

		CYN(new StaffEasterEggBuilder("1d70383f-21ba-4b8b-a0b4-6c327fbdade1")
			.food(Material.GOLD_NUGGET)
			.burpSound(Sound.ENTITY_PIGLIN_ADMIRING_ITEM)),

		MARSHY(new StaffEasterEggBuilder("a7fa3c9c-d3cb-494e-bff6-8b6d416b18e3")
			.food(Material.CHEST)
			.eatSound(Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM)
			.burpSound(Sound.ENTITY_ITEM_FRAME_BREAK)),
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
