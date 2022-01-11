package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.features.mobheads.variants.AxolotlVariant;
import gg.projecteden.nexus.features.mobheads.variants.CatVariant;
import gg.projecteden.nexus.features.mobheads.variants.CreeperVariant;
import gg.projecteden.nexus.features.mobheads.variants.FoxVariant;
import gg.projecteden.nexus.features.mobheads.variants.HorseVariant;
import gg.projecteden.nexus.features.mobheads.variants.LlamaVariant;
import gg.projecteden.nexus.features.mobheads.variants.MooshroomVariant;
import gg.projecteden.nexus.features.mobheads.variants.PandaVariant;
import gg.projecteden.nexus.features.mobheads.variants.ParrotVariant;
import gg.projecteden.nexus.features.mobheads.variants.RabbitVariant;
import gg.projecteden.nexus.features.mobheads.variants.SheepVariant;
import gg.projecteden.nexus.features.mobheads.variants.SnowmanVariant;
import gg.projecteden.nexus.features.mobheads.variants.TraderLlamaVariant;
import gg.projecteden.nexus.features.mobheads.variants.TropicalFishVariant;
import gg.projecteden.nexus.features.mobheads.variants.VillagerVariant;
import gg.projecteden.nexus.features.mobheads.variants.ZombieVillagerVariant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.Env;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static gg.projecteden.utils.RandomUtils.randomElement;

@Getter
public enum MobHeadType implements MobHead {
	AXOLOTL(EntityType.AXOLOTL,
		AxolotlVariant.class,
		entity -> AxolotlVariant.of((Axolotl) entity),
		(entity, type) -> ((Axolotl) entity).setVariant((Axolotl.Variant) type),
		() -> randomElement(Axolotl.Variant.class)),
	BAT(EntityType.BAT),
	BEE(EntityType.BEE),
	BLAZE(EntityType.BLAZE),
	CAT(EntityType.CAT,
		CatVariant.class,
		entity -> CatVariant.of((Cat) entity),
		(entity, type) -> ((Cat) entity).setCatType((Cat.Type) type),
		() -> randomElement(Cat.Type.class)),
	CAVE_SPIDER(EntityType.CAVE_SPIDER),
	CHICKEN(EntityType.CHICKEN),
	COD(EntityType.COD),
	COW(EntityType.COW),
	CREEPER(EntityType.CREEPER,
		CreeperVariant.class,
		entity -> CreeperVariant.of((Creeper) entity),
		(entity, type) -> ((Creeper) entity).setPowered((boolean) type),
		() -> randomElement(true, false)),
	DOLPHIN(EntityType.DOLPHIN),
	DONKEY(EntityType.DONKEY),
	DROWNED(EntityType.DROWNED),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
	ENDER_DRAGON(EntityType.ENDER_DRAGON),
	ENDERMAN(EntityType.ENDERMAN),
	ENDERMITE(EntityType.ENDERMITE),
	EVOKER(EntityType.EVOKER),
	FOX(EntityType.FOX,
		FoxVariant.class,
		entity -> FoxVariant.of((Fox) entity),
		(entity, type) -> ((Fox) entity).setFoxType((Fox.Type) type),
		() -> randomElement(Fox.Type.class)),
	GHAST(EntityType.GHAST),
	GLOW_SQUID(EntityType.GLOW_SQUID),
	GOAT(EntityType.GOAT),
	GUARDIAN(EntityType.GUARDIAN),
	HOGLIN(EntityType.HOGLIN),
	HORSE(EntityType.HORSE,
		HorseVariant.class,
		entity -> HorseVariant.of((Horse) entity),
		(entity, type) -> ((Horse) entity).setColor((Horse.Color) type),
		() -> randomElement(Horse.Color.class)),
	HUSK(EntityType.HUSK),
	ILLUSIONER(EntityType.ILLUSIONER),
	IRON_GOLEM(EntityType.IRON_GOLEM),
	LLAMA(EntityType.LLAMA,
		LlamaVariant.class,
		entity -> LlamaVariant.of((Llama) entity),
		(entity, type) -> ((Llama) entity).setColor((Llama.Color) type),
		() -> randomElement(Llama.Color.class)),
	MAGMA_CUBE(EntityType.MAGMA_CUBE),
	MUSHROOM_COW(EntityType.MUSHROOM_COW,
		MooshroomVariant.class,
		entity -> MooshroomVariant.of((MushroomCow) entity),
		(entity, type) -> ((MushroomCow) entity).setVariant((MushroomCow.Variant) type),
		() -> randomElement(MushroomCow.Variant.class)),
	MULE(EntityType.MULE),
	OCELOT(EntityType.OCELOT),
	PANDA(EntityType.PANDA,
		PandaVariant.class,
		entity -> PandaVariant.of((Panda) entity),
		(entity, type) -> ((Panda) entity).setMainGene((Panda.Gene) type),
		() -> randomElement(Panda.Gene.class)),
	PARROT(EntityType.PARROT,
		ParrotVariant.class,
		entity -> ParrotVariant.of((Parrot) entity),
		(entity, type) -> ((Parrot) entity).setVariant((Parrot.Variant) type),
		() -> randomElement(Parrot.Variant.class)),
	PHANTOM(EntityType.PHANTOM),
	PIG(EntityType.PIG),
	PIGLIN(EntityType.PIGLIN),
	PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE),
	PILLAGER(EntityType.PILLAGER),
	PLAYER(EntityType.PLAYER),
	POLAR_BEAR(EntityType.POLAR_BEAR),
	PUFFERFISH(EntityType.PUFFERFISH),
	RABBIT(EntityType.RABBIT,
		RabbitVariant.class,
		entity -> RabbitVariant.of((Rabbit) entity),
		(entity, type) -> ((Rabbit) entity).setRabbitType((Rabbit.Type) type),
		() -> randomElement(Rabbit.Type.class)),
	RAVAGER(EntityType.RAVAGER),
	SALMON(EntityType.SALMON),
	SHEEP(EntityType.SHEEP,
		SheepVariant.class,
		entity -> SheepVariant.of((Sheep) entity),
		(entity, type) -> ((Sheep) entity).setColor((DyeColor) type),
		() -> randomElement(DyeColor.class)),
	SHULKER(EntityType.SHULKER),
	SILVERFISH(EntityType.SILVERFISH),
	SKELETON(EntityType.SKELETON),
	SKELETON_HORSE(EntityType.SKELETON_HORSE),
	SLIME(EntityType.SLIME),
	SNOWMAN(EntityType.SNOWMAN,
		SnowmanVariant.class,
		entity -> SnowmanVariant.of((Snowman) entity),
		(entity, type) -> ((Snowman) entity).setDerp((boolean) type),
		() -> randomElement(true, false)),
	SPIDER(EntityType.SPIDER),
	SQUID(EntityType.SQUID),
	STRAY(EntityType.STRAY),
	STRIDER(EntityType.STRIDER),
	TRADER_LLAMA(EntityType.TRADER_LLAMA,
		TraderLlamaVariant.class,
		entity -> TraderLlamaVariant.of((TraderLlama) entity),
		(entity, type) -> ((TraderLlama) entity).setColor((TraderLlama.Color) type),
		() -> randomElement(TraderLlama.Color.class)),
	TROPICAL_FISH(EntityType.TROPICAL_FISH,
		TropicalFishVariant.class,
		entity -> TropicalFishVariant.random(),
		(entity, type) -> {},
		() -> null),
	TURTLE(EntityType.TURTLE),
	VEX(EntityType.VEX),
	VILLAGER(EntityType.VILLAGER,
		VillagerVariant.class,
		entity -> VillagerVariant.of((Villager) entity),
		(entity, type) -> ((Villager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)),
	VINDICATOR(EntityType.VINDICATOR),
	WANDERING_TRADER(EntityType.WANDERING_TRADER),
	WITCH(EntityType.WITCH),
	WITHER(EntityType.WITHER),
	WITHER_SKELETON(EntityType.WITHER_SKELETON),
	WOLF(EntityType.WOLF),
	ZOGLIN(EntityType.ZOGLIN),
	ZOMBIE(EntityType.ZOMBIE),
	ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER,
		ZombieVillagerVariant.class,
		entity -> ZombieVillagerVariant.of((ZombieVillager) entity),
		(entity, type) -> ((ZombieVillager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)),
	ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN),
	;

	private final EntityType entityType;
	private final Class<? extends MobHeadVariant> variantClass;
	private final Function<Entity, MobHeadVariant> variantConverter;
	private final BiConsumer<Entity, Object> variantSetter;
	private final Supplier<?> randomVariant;

	@Setter
	private ItemStack genericSkull;
	@Setter
	private double chance;

	@Getter
	private static final Set<ItemStack> allSkulls = new HashSet<>();

	MobHeadType(EntityType entityType) {
		this(entityType, null, null, null, null);
	}

	MobHeadType(
		EntityType entityType,
		Class<? extends MobHeadVariant> variantClass,
		Function<Entity, MobHeadVariant> variantConverter,
		BiConsumer<Entity, Object> variantSetter,
		Supplier<?> randomVariant
	) {
		this.entityType = entityType;
		this.variantClass = variantClass;
		this.variantConverter = variantConverter;
		this.variantSetter = variantSetter;
		this.randomVariant = randomVariant;
	}

	@Nullable
	public static MobHeadType of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from.equals(entry.getEntityType())).findFirst().orElse(null);
	}

	@Override
	public @NotNull MobHeadType getType() {
		return this;
	}

	public @Nullable ItemStack getSkull() {
		if (genericSkull == null)
			return null;
		return genericSkull.clone();
	}

	public ItemStack getSkull(MobHeadVariant variant) {
		return variant == null ? genericSkull : variant.getSkull();
	}

	public boolean hasVariants() {
		return getVariantClass() != null;
	}

	public List<MobHeadVariant> getVariants() {
		if (!hasVariants())
			return Collections.emptyList();

		return List.of(getVariantClass().getEnumConstants());
	}

	@Nullable
	public MobHeadVariant getVariant(Entity entity) {
		if (variantConverter == null)
			return null;

		return variantConverter.apply(entity);
	}

	private static final List<EntityType> EXCLUDED_TYPES = List.of(EntityType.ARMOR_STAND, EntityType.GIANT);

	public static List<EntityType> getExpectedTypes() {
		List<EntityType> expectedTypes = new ArrayList<>(Arrays.asList(EntityType.values()));
		expectedTypes.removeIf(entityType -> {
			if (entityType.getEntityClass() == null)
				return true;
			if (!LivingEntity.class.isAssignableFrom(entityType.getEntityClass()))
				return true;
			if (EXCLUDED_TYPES.contains(entityType))
				return true;

			return false;
		});

		return expectedTypes;
	}

	public static List<EntityType> getMissingTypes() {
		final List<EntityType> expectedTypes = getExpectedTypes();

		for (MobHeadType mobHeadType : MobHeadType.values())
			expectedTypes.remove(mobHeadType.getEntityType());

		return expectedTypes;
	}

	static void load() {
		World world = Bukkit.getWorld(Nexus.getEnv() == Env.PROD ? "server" : "world");
		if (world == null)
			return;

		WorldGuardUtils worldguard = new WorldGuardUtils(world);
		WorldEditUtils worldedit = new WorldEditUtils(world);

		for (Block block : worldedit.getBlocks(worldguard.getRegion("mobheads"))) {
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
					Nexus.warn("[MobHeads] Cannot parse entity type: " + entity);
					continue;
				}

				final MobHeadType mobHeadType = MobHeadType.of(type);
				double chance = Double.parseDouble(sign.getLine(3));

				if (mobHeadType == null) {
					Nexus.warn("[MobHeads] Found EntityType with no MobHeadType: " + type);
					return;
				}

				skull = new ItemBuilder(skull).name("&e" + mobHeadType.getDisplayName() + " Head").lore("&3Mob Head").build();
				mobHeadType.setGenericSkull(skull);
				mobHeadType.setChance(chance);
				allSkulls.add(skull);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Reflections reflections = new Reflections(MobHeadType.class.getPackage().getName());
		for (Class<? extends MobHeadVariant> variant : reflections.getSubTypesOf(MobHeadVariant.class)) {
			for (Block block : worldedit.getBlocks(worldguard.getRegion("mobheads_variant_" + variant.getSimpleName().replace("Variant", "").toLowerCase()))) {
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

					skull = new ItemBuilder(skull).name("&e" + mobHeadVariant.getDisplayName() + " Head").lore("&3Mob Head").build();
					mobHeadVariant.setItemStack(skull);
					allSkulls.add(skull);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
