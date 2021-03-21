package me.pugabyte.nexus.features.quests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum MobTest {

	AXOLOTL(null, null),
	BAT(EntityType.BAT, null),
	BEE(EntityType.BEE, null),
	BLAZE(EntityType.BLAZE, null),
	CAT(EntityType.CAT, entity -> CatType.of((Cat) entity).getItemStack()),
	CAVE_SPIDER(EntityType.CAVE_SPIDER, null),
	CHICKEN(EntityType.CHICKEN, null),
	COD(EntityType.COD, null),
	COW(EntityType.COW, null),
	CREEPER(EntityType.CREEPER, entity -> CreeperType.of((Creeper) entity).getItemStack()),
	DOLPHIN(EntityType.DOLPHIN, null),
	DONKEY(EntityType.DONKEY, null),
	DROWNED(EntityType.DROWNED, null),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, null),
	ENDER_DRAGON(EntityType.ENDER_DRAGON, null),
	ENDERMAN(EntityType.ENDERMAN, null),
	ENDERMITE(EntityType.ENDERMITE, null),
	EVOKER(EntityType.EVOKER, null),
	FOX(EntityType.FOX, entity -> FoxType.of((Fox) entity).getItemStack()),
	GHAST(EntityType.GHAST, null),
	GLOW_SQUID(null, null),
	GOAT(null, null),
	GUARDIAN(EntityType.GUARDIAN, null),
	HOGLIN(EntityType.HOGLIN, null),
	HORSE(EntityType.HORSE, entity -> HorseStyle.of((Horse) entity).getItemStack()),
	HUSK(EntityType.HUSK, null),
	ILLUSIONER(EntityType.ILLUSIONER, null),
	IRON_GOLEM(EntityType.IRON_GOLEM, null),
	LLAMA(EntityType.LLAMA, entity -> LlamaColor.of((Llama) entity).getItemStack()),
	MAGMA_CUBE(EntityType.MAGMA_CUBE, null),
	MUSHROOM_COW(EntityType.MUSHROOM_COW, entity -> MooshroomType.of((MushroomCow) entity).getItemStack()),
	MULE(EntityType.MULE, null),
	OCELOT(EntityType.OCELOT, null),
	PANDA(EntityType.PANDA, entity -> PandaGene.of((Panda) entity).getItemStack()),
	PARROT(EntityType.PARROT, entity -> ParrotVariant.of((Parrot) entity).getItemStack()),
	PHANTOM(EntityType.PHANTOM, null),
	PIG(EntityType.PIG, null),
	PIGLIN(EntityType.PIGLIN, null),
	PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, null),
	PILLAGER(EntityType.PILLAGER, null),
	PLAYER(EntityType.PLAYER, null),
	POLAR_BEAR(EntityType.POLAR_BEAR, null),
	PUFFERFISH(EntityType.PUFFERFISH, null),
	RABBIT(EntityType.RABBIT, entity -> RabbitType.of((Rabbit) entity).getItemStack()),
	RAVAGER(EntityType.RAVAGER, null),
	SALMON(EntityType.SALMON, null),
	SHEEP(EntityType.SHEEP, entity -> SheepColor.of((Sheep) entity).getItemStack()),
	SHULKER(EntityType.SHULKER, null),
	SILVERFISH(EntityType.SILVERFISH, null),
	SKELETON(EntityType.SKELETON, null),
	SKELETON_HORSE(EntityType.SKELETON_HORSE, null),
	SLIME(EntityType.SLIME, null),
	SNOWMAN(EntityType.SNOWMAN, entity -> SnowmanType.of((Snowman) entity).getItemStack()),
	SPIDER(EntityType.SPIDER, null),
	SQUID(EntityType.SQUID, null),
	STRAY(EntityType.STRAY, null),
	STRIDER(EntityType.STRIDER, null),
	TRADER_LLAMA(EntityType.TRADER_LLAMA, entity -> LlamaColor.of((TraderLlama) entity).getItemStack()),
	TROPICAL_FISH(EntityType.TROPICAL_FISH, entity -> TropicalFishType.RANDOM.getItemStack()),
	TURTLE(EntityType.TURTLE, null),
	VEX(EntityType.VEX, null),
	VILLAGER(EntityType.VILLAGER, entity -> VillagerProfession.of((Villager) entity).getItemStack()),
	VINDICATOR(EntityType.VINDICATOR, null),
	WANDERING_TRADER(EntityType.WANDERING_TRADER, null),
	WITCH(EntityType.WITCH, null),
	WITHER(EntityType.WITHER, null),
	WITHER_SKELETON(EntityType.WITHER_SKELETON, null),
	WOLF(EntityType.WOLF, null),
	ZOGLIN(EntityType.ZOGLIN, null),
	ZOMBIE(EntityType.ZOMBIE, null),
	ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, null),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, entity -> VillagerProfession.of((ZombieVillager) entity).getItemStack()),
	ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, null),
	;

	private static MobTest of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(null);
	}

	private final EntityType type;
	@Setter
	private ItemStack generic;
	private final Function<Entity, ItemStack> variant;

	@Getter
	private static final Map<EntityType, Double> mobChance = new HashMap<>();

	private static void setEntityGenericHead(EntityType type, ItemStack generic) {
		MobTest.of(type).setGeneric(generic);
	}

	private static void setEntityVariantHead(EntityType type, ItemStack variant) {
		// TODO: get pugs help
//		SheepColor.RED.setItemStack(variant);
	}

	public static ItemStack getEntityHead(Entity entity) {
		MobTest mobTest = MobTest.of(entity.getType());
		ItemStack skull = mobTest.getVariant().apply(entity);
		if (skull == null)
			skull = mobTest.getGeneric();
		return skull;
	}

	public static ItemStack getEntityTypeHead(EntityType type) {
		return MobTest.of(type).getGeneric();
	}

	// SHEEP
	@Getter
	@RequiredArgsConstructor
	private enum SheepColor {
		NONE(null),
		RED(ColorType.RED),
		ORANGE(ColorType.ORANGE),
		YELLOW(ColorType.YELLOW),
		LIGHT_GREEN(ColorType.LIGHT_GREEN),
		GREEN(ColorType.GREEN),
		CYAN(ColorType.CYAN),
		LIGHT_BLUE(ColorType.LIGHT_BLUE),
		BLUE(ColorType.BLUE),
		PURPLE(ColorType.PURPLE),
		MAGENTA(ColorType.MAGENTA),
		PINK(ColorType.PINK),
		BROWN(ColorType.BROWN),
		BLACK(ColorType.BLACK),
		GRAY(ColorType.GRAY),
		LIGHT_GRAY(ColorType.LIGHT_GRAY),
		WHITE(ColorType.WHITE),
		;

		private final ColorType type;
		@Setter
		private ItemStack itemStack;

		public static SheepColor of(Sheep sheep) {
			ColorType from = ColorType.of(sheep.getColor());
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// HORSE
	@Getter
	@RequiredArgsConstructor
	private enum HorseStyle {
		NONE(null),
		WHITE(Style.WHITE),
		BLACK_DOTS(Style.BLACK_DOTS),
		WHITE_DOTS(Style.WHITE_DOTS),
		WHITEFIELD(Style.WHITEFIELD),
		;

		private final Horse.Style type;
		@Setter
		private ItemStack itemStack;

		public static HorseStyle of(Horse horse) {
			Horse.Style from = horse.getStyle();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// CAT
	@Getter
	@RequiredArgsConstructor
	private enum CatType {
		NONE(null),
		BLACK(Type.BLACK),
		WHITE(Type.WHITE),
		ALL_BLACK(Type.ALL_BLACK),
		RED(Type.RED),
		BRITISH_SHORTHAIR(Type.BRITISH_SHORTHAIR),
		CALICO(Type.CALICO),
		JELLIE(Type.JELLIE),
		PERSIAN(Type.PERSIAN),
		RAGDOLL(Type.RAGDOLL),
		SIAMESE(Type.SIAMESE),
		TABBY(Type.TABBY),
		;

		private final Cat.Type type;
		@Setter
		private ItemStack itemStack;

		public static CatType of(Cat cat) {
			Cat.Type from = cat.getCatType();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// RABBIT
	@Getter
	@RequiredArgsConstructor
	private enum RabbitType {
		NONE(null),
		BLACK(Rabbit.Type.BLACK),
		WHITE(Rabbit.Type.WHITE),
		BROWN(Rabbit.Type.BROWN),
		BLACK_AND_WHITE(Rabbit.Type.BLACK_AND_WHITE),
		GOLD(Rabbit.Type.GOLD),
		SALT_AND_PEPPER(Rabbit.Type.SALT_AND_PEPPER),
		;

		private final Rabbit.Type type;
		@Setter
		private ItemStack itemStack;

		public static RabbitType of(Rabbit rabbit) {
			Rabbit.Type from = rabbit.getRabbitType();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// TROPICAL FISH
	@Getter
	@RequiredArgsConstructor
	private enum TropicalFishType {
		RANDOM(),
		;

		@Setter
		private List<ItemStack> itemStacks;

		public ItemStack getItemStack() {
			return RandomUtils.randomElement(itemStacks);
		}
	}

	// PARROT
	@Getter
	@RequiredArgsConstructor
	private enum ParrotVariant {
		NONE(null),
		BLUE(Parrot.Variant.BLUE),
		RED(Parrot.Variant.RED),
		CYAN(Parrot.Variant.CYAN),
		GRAY(Parrot.Variant.GRAY),
		GREEN(Parrot.Variant.GREEN),
		;

		private final Parrot.Variant type;
		@Setter
		private ItemStack itemStack;

		public static ParrotVariant of(Parrot parrot) {
			Parrot.Variant from = parrot.getVariant();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// FOX
	@Getter
	@RequiredArgsConstructor
	private enum FoxType {
		NONE(null),
		RED(Fox.Type.RED),
		SNOW(Fox.Type.SNOW),
		;

		private final Fox.Type type;
		@Setter
		private ItemStack itemStack;

		public static FoxType of(Fox fox) {
			Fox.Type from = fox.getFoxType();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// CREEPER
	@Getter
	@RequiredArgsConstructor
	private enum CreeperType {
		NONE(),
		POWERED(),
		;

		@Setter
		private ItemStack itemStack;

		public static CreeperType of(Creeper creeper) {
			return creeper.isPowered() ? POWERED : NONE;
		}
	}

	// PANDA
	@Getter
	@RequiredArgsConstructor
	private enum PandaGene {
		NONE(null),
		BROWN(Gene.BROWN),
		AGGRESSIVE(Gene.AGGRESSIVE),
		LAZY(Gene.LAZY),
		NORMAL(Gene.NORMAL),
		PLAYFUL(Gene.PLAYFUL),
		WEAK(Gene.WEAK),
		WORRIED(Gene.WORRIED),
		;

		private final Panda.Gene type;
		@Setter
		private ItemStack itemStack;

		public static PandaGene of(Panda panda) {
			Panda.Gene from = panda.getMainGene();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// SNOW GOLEM
	@Getter
	@RequiredArgsConstructor
	private enum SnowmanType {
		NONE(),
		DERP(),
		;

		@Setter
		private ItemStack itemStack;

		public static SnowmanType of(Snowman snowman) {
			return snowman.isDerp() ? DERP : NONE;
		}

	}

	// VILLAGER
	// ZOMBIE VILLAGER
	@Getter
	@RequiredArgsConstructor
	private enum VillagerProfession {
		NONE(null),
		ARMORER(Profession.ARMORER),
		BUTCHER(Profession.BUTCHER),
		CARTOGRAPHER(Profession.CARTOGRAPHER),
		CLERIC(Profession.CLERIC),
		FARMER(Profession.FARMER),
		FISHERMAN(Profession.FISHERMAN),
		FLETCHER(Profession.FLETCHER),
		LEATHERWORKER(Profession.LEATHERWORKER),
		LIBRARIAN(Profession.LIBRARIAN),
		MASON(Profession.MASON),
		NITWIT(Profession.NITWIT),
		SHEPHERD(Profession.SHEPHERD),
		TOOLSMITH(Profession.TOOLSMITH),
		WEAPONSMITH(Profession.WEAPONSMITH),
		;

		private final Villager.Profession type;
		@Setter
		private ItemStack itemStack;

		public static VillagerProfession of(ZombieVillager zombieVillager) {
			return of(zombieVillager.getVillagerProfession());
		}

		public static VillagerProfession of(Villager villager) {
			return of(villager.getProfession());
		}

		private static VillagerProfession of(Villager.Profession from) {
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// LLAMA
	// TRADER LLAMA
	@Getter
	@RequiredArgsConstructor
	private enum LlamaColor {
		NONE(null),
		GRAY(Color.GRAY),
		WHITE(Color.WHITE),
		BROWN(Color.BROWN),
		CREAMY(Color.CREAMY),
		;

		private final Llama.Color type;
		@Setter
		private ItemStack itemStack;

		public static LlamaColor of(Llama llama) {
			return of(llama.getColor());
		}

		public static LlamaColor of(TraderLlama traderLlama) {
			return of(traderLlama.getColor());
		}

		private static LlamaColor of(Llama.Color from) {
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	// MOOSHROOM
	@Getter
	@RequiredArgsConstructor
	private enum MooshroomType {
		NONE(null),
		RED(Variant.RED),
		BROWN(Variant.BROWN),
		;

		private final MushroomCow.Variant type;
		@Setter
		private ItemStack itemStack;

		public static MooshroomType of(MushroomCow mushroomCow) {
			MushroomCow.Variant from = mushroomCow.getVariant();
			return Arrays.stream(values()).filter(entry -> from.equals(entry.getType())).findFirst().orElse(NONE);
		}
	}

	static {
		World world = Bukkit.getWorld("survival");
		WorldGuardUtils WGUtils = new WorldGuardUtils(world);
		WorldEditUtils WEUtils = new WorldEditUtils(world);

		for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads"))) {
			if (!MaterialTag.SIGNS.isTagged(block.getType()))
				continue;

			Sign sign = (Sign) block.getState();
			Directional directional = (Directional) sign.getBlockData();
			ItemStack skull = block.getRelative(directional.getFacing().getOppositeFace()).getRelative(BlockFace.UP)
					.getDrops().stream().findFirst().orElse(null);
			if (skull == null)
				continue;

			EntityType type;
			String entity = (sign.getLine(0) + sign.getLine(1)).trim();
			try {
				type = EntityType.valueOf(entity);
			} catch (Exception ignored) {
				Nexus.log("Cannot parse entity type: " + entity);
				continue;
			}

			double chance = Double.parseDouble(sign.getLine(3));

			skull = new ItemBuilder(skull).name("&e" + StringUtils.camelCase(type) + " Head").build();
			setEntityGenericHead(type, skull);
			mobChance.put(type, chance);
		}
	}
}
