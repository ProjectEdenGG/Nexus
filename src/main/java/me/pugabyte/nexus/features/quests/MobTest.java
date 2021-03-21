package me.pugabyte.nexus.features.quests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
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
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

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
	TRADER_LLAMA(EntityType.TRADER_LLAMA, entity -> TraderLlamaColor.of((TraderLlama) entity).getItemStack()),
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
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, entity -> ZombieVillagerProfession.of((ZombieVillager) entity).getItemStack()),
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

	private interface MobHeadVariant {
		EntityType getEntityType();
		void setItemStack(ItemStack itemStack);
	}

	@Getter
	@RequiredArgsConstructor
	private enum SheepColor implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.SHEEP;
		}

		public static SheepColor of(Sheep sheep) {
			return Arrays.stream(values()).filter(entry -> ColorType.of(sheep.getColor()) == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum HorseStyle implements MobHeadVariant {
		NONE(null),
		WHITE(Horse.Color.WHITE),
		CREAMY(Horse.Color.CREAMY),
		CHESTNUT(Horse.Color.CHESTNUT),
		BROWN(Horse.Color.BROWN),
		BLACK(Horse.Color.BLACK),
		GRAY(Horse.Color.GRAY),
		DARK_BROWN(Horse.Color.DARK_BROWN),
		;

		private final Horse.Color type;
		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.HORSE;
		}

		public static HorseStyle of(Horse horse) {
			return Arrays.stream(values()).filter(entry -> horse.getColor() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum CatType implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.CAT;
		}

		private final Cat.Type type;
		@Setter
		private ItemStack itemStack;

		public static CatType of(Cat cat) {
			return Arrays.stream(values()).filter(entry -> cat.getCatType() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum RabbitType implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.RABBIT;
		}

		public static RabbitType of(Rabbit rabbit) {
			return Arrays.stream(values()).filter(entry -> rabbit.getRabbitType() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum TropicalFishType implements MobHeadVariant {
		RANDOM,
		;

		@Setter
		private List<ItemStack> itemStacks = new ArrayList<>();

		public ItemStack getItemStack() {
			return RandomUtils.randomElement(itemStacks);
		}

		@Override
		public EntityType getEntityType() {
			return EntityType.TROPICAL_FISH;
		}

		public void setItemStack(ItemStack itemStack) {
			itemStacks.add(itemStack);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum ParrotVariant implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.PARROT;
		}

		public static ParrotVariant of(Parrot parrot) {
			return Arrays.stream(values()).filter(entry -> parrot.getVariant() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum FoxType implements MobHeadVariant {
		NONE(null),
		RED(Fox.Type.RED),
		SNOW(Fox.Type.SNOW),
		;

		private final Fox.Type type;
		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.FOX;
		}

		public static FoxType of(Fox fox) {
			return Arrays.stream(values()).filter(entry -> fox.getFoxType() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum CreeperType implements MobHeadVariant {
		NONE,
		POWERED,
		;

		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.CREEPER;
		}

		public static CreeperType of(Creeper creeper) {
			return creeper.isPowered() ? POWERED : NONE;
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum PandaGene implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.PANDA;
		}

		public static PandaGene of(Panda panda) {
			return Arrays.stream(values()).filter(entry -> panda.getMainGene() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum SnowmanType implements MobHeadVariant {
		NONE,
		DERP,
		;

		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.SNOWMAN;
		}

		public static SnowmanType of(Snowman snowman) {
			return snowman.isDerp() ? DERP : NONE;
		}

	}

	@Getter
	@RequiredArgsConstructor
	private enum VillagerProfession implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.VILLAGER;
		}

		private static VillagerProfession of(Villager villager) {
			return Arrays.stream(values()).filter(entry -> villager.getProfession() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum ZombieVillagerProfession implements MobHeadVariant {
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

		@Override
		public EntityType getEntityType() {
			return EntityType.VILLAGER;
		}

		public static ZombieVillagerProfession of(ZombieVillager zombieVillager) {
			return Arrays.stream(values()).filter(entry -> zombieVillager.getVillagerProfession() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum LlamaColor implements MobHeadVariant {
		NONE(null),
		GRAY(Color.GRAY),
		WHITE(Color.WHITE),
		BROWN(Color.BROWN),
		CREAMY(Color.CREAMY),
		;

		private final Llama.Color type;
		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.LLAMA;
		}

		private static LlamaColor of(Llama llama) {
			return Arrays.stream(values()).filter(entry -> llama.getColor() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum TraderLlamaColor implements MobHeadVariant {
		NONE(null),
		GRAY(Color.GRAY),
		WHITE(Color.WHITE),
		BROWN(Color.BROWN),
		CREAMY(Color.CREAMY),
		;

		private final Llama.Color type;
		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.TRADER_LLAMA;
		}

		public static TraderLlamaColor of(TraderLlama traderLlama) {
			return Arrays.stream(values()).filter(entry -> traderLlama.getColor() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private enum MooshroomType implements MobHeadVariant {
		NONE(null),
		RED(Variant.RED),
		BROWN(Variant.BROWN),
		;

		private final MushroomCow.Variant type;
		@Setter
		private ItemStack itemStack;

		@Override
		public EntityType getEntityType() {
			return EntityType.MUSHROOM_COW;
		}

		public static MooshroomType of(MushroomCow mushroomCow) {
			return Arrays.stream(values()).filter(entry -> mushroomCow.getVariant() == entry.getType()).findFirst().orElse(NONE);
		}
	}

	static {
		World world = Bukkit.getWorld("survival");
		WorldGuardUtils WGUtils = new WorldGuardUtils(world);
		WorldEditUtils WEUtils = new WorldEditUtils(world);

		for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads"))) {
			try {
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

				skull = new ItemBuilder(skull).name("&e" + camelCase(type) + " Head").build();
				MobTest.of(type).setGeneric(skull);
				mobChance.put(type, chance);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Reflections reflections = new Reflections(MobTest.class.getPackage().getName());
		for (Class<? extends MobHeadVariant> variant : reflections.getSubTypesOf(MobHeadVariant.class)) {
			for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads_variant_" + variant.getSimpleName().toLowerCase()))) {
				try {
					if (!MaterialTag.SIGNS.isTagged(block.getType()))
						continue;

					Sign sign = (Sign) block.getState();
					Directional directional = (Directional) sign.getBlockData();
					ItemStack skull = block.getRelative(directional.getFacing().getOppositeFace())
							.getRelative(BlockFace.UP)
							.getDrops().stream()
							.findFirst().orElse(null);

					if (skull == null)
						continue;

					MobHeadVariant mobHeadVariant;
					String variantType = (sign.getLine(0) + sign.getLine(1)).trim();
					try {
						mobHeadVariant = EnumUtils.valueOf(variant, variantType);
					} catch (Exception ignored) {
						Nexus.log("Cannot parse entity variant: " + variant.getSimpleName() + "." + variantType);
						continue;
					}

					skull = new ItemBuilder(skull).name("&e" + camelCase((Enum<?>) mobHeadVariant) + " " + camelCase(mobHeadVariant.getEntityType()) + " Head").build();
					mobHeadVariant.setItemStack(skull);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
