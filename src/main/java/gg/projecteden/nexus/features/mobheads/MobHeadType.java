package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.common.HeadConfig;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.features.mobheads.variants.AxolotlVariant;
import gg.projecteden.nexus.features.mobheads.variants.CatVariant;
import gg.projecteden.nexus.features.mobheads.variants.CreeperVariant;
import gg.projecteden.nexus.features.mobheads.variants.FoxVariant;
import gg.projecteden.nexus.features.mobheads.variants.FrogVariant;
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
import gg.projecteden.nexus.models.mobheads.MobHeadChanceConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public enum MobHeadType implements MobHead {
	@HeadConfig(headId = "51477")
	ALLAY(Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM),

	@HeadConfig(headId = "91910")
	ARMADILLO(Sound.ENTITY_ARMADILLO_AMBIENT),

	@HeadConfig(headId = "41592", variantClass = AxolotlVariant.class)
	AXOLOTL(
		entity -> AxolotlVariant.of((Axolotl) entity),
		(entity, type) -> ((Axolotl) entity).setVariant((Axolotl.Variant) type),
		() -> RandomUtils.randomElement(Axolotl.Variant.class),
		Sound.ENTITY_AXOLOTL_IDLE_AIR
	),

	@HeadConfig(headId = "26033")
	BAT(Sound.ENTITY_BAT_AMBIENT),

	@HeadConfig(headId = "31260")
	BEE(Sound.ENTITY_BEE_POLLINATE),

	@HeadConfig(headId = "322")
	BLAZE(Sound.ENTITY_BLAZE_AMBIENT),

	@HeadConfig(headId = "87691")
	BOGGED(Sound.ENTITY_BOGGED_AMBIENT),

	@HeadConfig(headId = "68476")
	BREEZE(Sound.ENTITY_BREEZE_IDLE_AIR),

	@HeadConfig(headId = "58939")
	CAMEL(Sound.ENTITY_CAMEL_STAND),

	@HeadConfig(headId = "14189", variantClass = CatVariant.class)
	CAT(
		entity -> CatVariant.of((Cat) entity),
		(entity, type) -> ((Cat) entity).setCatType((Cat.Type) type),
		() -> RandomUtils.randomElement(Cat.Type.class),
		Sound.ENTITY_CAT_AMBIENT
	),

	@HeadConfig(headId = "315")
	CAVE_SPIDER(Sound.ENTITY_SPIDER_AMBIENT),

	@HeadConfig(headId = "336")
	CHICKEN(Sound.ENTITY_CHICKEN_AMBIENT),

	@HeadConfig(headId = "17898")
	COD(Sound.ENTITY_COD_FLOP),

	@HeadConfig(headId = "22866")
	COW(Sound.ENTITY_COW_AMBIENT),

	@HeadConfig(headId = "106826")
	CREAKING(Sound.ENTITY_CREAKING_AMBIENT),

	@HeadConfig(headType = Material.CREEPER_HEAD, variantClass = CreeperVariant.class)
	CREEPER(
		entity -> CreeperVariant.of((Creeper) entity),
		(entity, type) -> ((Creeper) entity).setPowered((boolean) type),
		() -> RandomUtils.randomElement(true, false),
		Sound.ENTITY_CREEPER_PRIMED
	),

	@HeadConfig(headId = "16799")
	DOLPHIN(Sound.ENTITY_DOLPHIN_AMBIENT_WATER),

	@HeadConfig(headId = "18144")
	DONKEY(Sound.ENTITY_DONKEY_ANGRY),

	@HeadConfig(headId = "15967")
	DROWNED(Sound.ENTITY_DROWNED_AMBIENT),

	@HeadConfig(headId = "25357")
	ELDER_GUARDIAN(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT),

	@HeadConfig(headType = Material.DRAGON_HEAD)
	ENDER_DRAGON(Sound.ENTITY_ENDER_DRAGON_AMBIENT),

	@HeadConfig(headId = "318")
	ENDERMAN(Sound.ENTITY_ENDERMAN_AMBIENT),

	@HeadConfig(headId = "18427")
	ENDERMITE(Sound.ENTITY_ENDERMITE_AMBIENT),

	@HeadConfig(headId = "3862")
	EVOKER(Sound.ENTITY_EVOKER_AMBIENT),

	@HeadConfig(headId = "33923", variantClass = FoxVariant.class)
	FOX(
		entity -> FoxVariant.of((Fox) entity),
		(entity, type) -> ((Fox) entity).setFoxType((Fox.Type) type),
		() -> RandomUtils.randomElement(Fox.Type.class),
		Sound.ENTITY_FOX_AMBIENT
	),

	@HeadConfig(headId = "51343", variantClass = FrogVariant.class)
	FROG(
		entity -> FrogVariant.of((Frog) entity),
		(entity, type) -> ((Frog) entity).setVariant((Frog.Variant) type),
		() -> RandomUtils.randomElement(Frog.Variant.class),
		Sound.ENTITY_FROG_HURT
	),

	@HeadConfig(headId = "321")
	GHAST(Sound.ENTITY_GHAST_AMBIENT),

	@HeadConfig(headId = "40441")
	GLOW_SQUID(Sound.ENTITY_GLOW_SQUID_AMBIENT),

	@HeadConfig(headId = "25213")
	GOAT(Sound.ENTITY_GOAT_AMBIENT),

	@HeadConfig(headId = "3135")
	GUARDIAN(Sound.ENTITY_GUARDIAN_AMBIENT_LAND),

	@HeadConfig(headId = "117203")
	HAPPY_GHAST(Sound.ENTITY_HAPPY_GHAST_AMBIENT),

	@HeadConfig(headId = "34783")
	HOGLIN(Sound.ENTITY_HOGLIN_ANGRY),

	@HeadConfig(headId = "1154", variantClass = HorseVariant.class)
	HORSE(
		entity -> HorseVariant.of((Horse) entity),
		(entity, type) -> ((Horse) entity).setColor((Horse.Color) type),
		() -> RandomUtils.randomElement(Horse.Color.class),
		Sound.ENTITY_HORSE_AMBIENT
	),

	@HeadConfig(headId = "37860")
	HUSK(Sound.ENTITY_HUSK_AMBIENT),

	@HeadConfig(headId = "35706")
	ILLUSIONER(Sound.ENTITY_ILLUSIONER_AMBIENT),

	@HeadConfig(headId = "33179")
	IRON_GOLEM(Sound.ENTITY_IRON_GOLEM_HURT),

	@HeadConfig(headId = "25376", variantClass = LlamaVariant.class)
	LLAMA(
		entity -> LlamaVariant.of((Llama) entity),
		(entity, type) -> ((Llama) entity).setColor((Llama.Color) type),
		() -> RandomUtils.randomElement(Llama.Color.class),
		Sound.ENTITY_LLAMA_AMBIENT
	),

	@HeadConfig(headId = "323")
	MAGMA_CUBE(Sound.ENTITY_MAGMA_CUBE_JUMP),

	@HeadConfig(headId = "339", variantClass = MooshroomVariant.class)
	MUSHROOM_COW(
		entity -> MooshroomVariant.of((MushroomCow) entity),
		(entity, type) -> ((MushroomCow) entity).setVariant((MushroomCow.Variant) type),
		() -> RandomUtils.randomElement(MushroomCow.Variant.class),
		Sound.ENTITY_COW_AMBIENT
	),

	@HeadConfig(headId = "3918")
	MULE(Sound.ENTITY_MULE_ANGRY),

	@HeadConfig(headId = "340")
	OCELOT(Sound.ENTITY_OCELOT_AMBIENT),

	@HeadConfig(headId = "6538", variantClass = PandaVariant.class)
	PANDA(
		entity -> PandaVariant.of((Panda) entity),
		(entity, type) -> ((Panda) entity).setMainGene((Panda.Gene) type),
		() -> RandomUtils.randomElement(Panda.Gene.class),
		Sound.ENTITY_PANDA_AMBIENT
	),

	@HeadConfig(headId = "34702", variantClass = ParrotVariant.class)
	PARROT(
		entity -> ParrotVariant.of((Parrot) entity),
		(entity, type) -> ((Parrot) entity).setVariant((Parrot.Variant) type),
		() -> RandomUtils.randomElement(Parrot.Variant.class),
		Sound.ENTITY_PARROT_AMBIENT
	),

	@HeadConfig(headId = "18091")
	PHANTOM(Sound.ENTITY_PHANTOM_AMBIENT),

	@HeadConfig(headId = "337")
	PIG(Sound.ENTITY_PIG_AMBIENT),

	@HeadConfig(headType = Material.PIGLIN_HEAD) // LEGACY HEAD = "37258"
	PIGLIN(Sound.ENTITY_PIGLIN_AMBIENT),

	@HeadConfig(headId = "38372")
	PIGLIN_BRUTE(Sound.ENTITY_PIGLIN_BRUTE_AMBIENT),

	@HeadConfig(headId = "25149")
	PILLAGER(Sound.ENTITY_PILLAGER_AMBIENT),

	@HeadConfig
	PLAYER,

	@HeadConfig(headId = "18379")
	POLAR_BEAR(Sound.ENTITY_POLAR_BEAR_AMBIENT),

	@HeadConfig(headId = "17900")
	PUFFERFISH(Sound.ENTITY_PUFFER_FISH_BLOW_UP),

	@HeadConfig(headId = "3933", variantClass = RabbitVariant.class)
	RABBIT(
		entity -> RabbitVariant.of((Rabbit) entity),
		(entity, type) -> ((Rabbit) entity).setRabbitType((Rabbit.Type) type),
		() -> RandomUtils.randomElement(Rabbit.Type.class),
		Sound.ENTITY_RABBIT_HURT
	),

	@HeadConfig(headId = "28196")
	RAVAGER(Sound.ENTITY_RAVAGER_AMBIENT),

	@HeadConfig(headId = "31623")
	SALMON(Sound.ENTITY_SALMON_FLOP),

	@HeadConfig(headId = "334", variantClass = SheepVariant.class)
	SHEEP(
		entity -> SheepVariant.of((Sheep) entity),
		(entity, type) -> ((Sheep) entity).setColor((DyeColor) type),
		() -> RandomUtils.randomElement(DyeColor.class),
		Sound.ENTITY_SHEEP_AMBIENT
	),

	@HeadConfig(headId = "38317")
	SHULKER(Sound.ENTITY_SHULKER_AMBIENT),

	@HeadConfig(headId = "3936")
	SILVERFISH(Sound.ENTITY_SILVERFISH_AMBIENT),

	@HeadConfig(headType = Material.SKELETON_SKULL)
	SKELETON(Sound.ENTITY_SKELETON_AMBIENT),

	@HeadConfig(headId = "6013")
	SKELETON_HORSE(Sound.ENTITY_SKELETON_HORSE_AMBIENT),

	@HeadConfig(headId = "17992")
	SLIME(Sound.ENTITY_SLIME_JUMP),

	@HeadConfig(headId = "60630")
	SNIFFER(Sound.ENTITY_SNIFFER_HAPPY),

	@HeadConfig(headId = "30000", variantClass = SnowmanVariant.class)
	SNOWMAN(
		entity -> SnowmanVariant.of((Snowman) entity),
		(entity, type) -> ((Snowman) entity).setDerp((boolean) type),
		() -> RandomUtils.randomElement(true, false),
		Sound.ENTITY_SNOW_GOLEM_DEATH
	),

	@HeadConfig(headId = "317")
	SPIDER(Sound.ENTITY_SPIDER_AMBIENT),

	@HeadConfig(headId = "12237")
	SQUID(Sound.ENTITY_SQUID_AMBIENT),

	@HeadConfig(headId = "22401")
	STRAY(Sound.ENTITY_STRAY_AMBIENT),

	@HeadConfig(headId = "35431")
	STRIDER(Sound.ENTITY_STRIDER_AMBIENT),

	@HeadConfig(headId = "50682")
	TADPOLE(Sound.ENTITY_TADPOLE_GROW_UP),

	@HeadConfig(headId = "26960", variantClass = TraderLlamaVariant.class)
	TRADER_LLAMA(
		entity -> TraderLlamaVariant.of((TraderLlama) entity),
		(entity, type) -> ((TraderLlama) entity).setColor((TraderLlama.Color) type),
		() -> RandomUtils.randomElement(TraderLlama.Color.class),
		Sound.ENTITY_LLAMA_AMBIENT
	),

	@HeadConfig(headId = "30233", variantClass = TropicalFishVariant.class)
	TROPICAL_FISH(
		entity -> TropicalFishVariant.random(),
		(entity, type) -> {
		},
		() -> null,
		Sound.ENTITY_TROPICAL_FISH_FLOP
	),

	@HeadConfig(headId = "17929")
	TURTLE(Sound.ENTITY_TURTLE_EGG_HATCH),

	@HeadConfig(headId = "3080")
	VEX(Sound.ENTITY_VEX_AMBIENT),

	@HeadConfig(headId = "12199", variantClass = VillagerVariant.class)
	VILLAGER(
		entity -> VillagerVariant.of((Villager) entity),
		(entity, type) -> ((Villager) entity).setVillagerType((Villager.Type) type),
		() -> RandomUtils.randomElement(Villager.Type.class),
		Sound.ENTITY_VILLAGER_AMBIENT
	),

	@HeadConfig(headId = "28323")
	VINDICATOR(Sound.ENTITY_VINDICATOR_AMBIENT),

	@HeadConfig(headId = "25676")
	WANDERING_TRADER(Sound.ENTITY_WANDERING_TRADER_AMBIENT),

	@HeadConfig(headId = "52282")
	WARDEN(Sound.ENTITY_WARDEN_AMBIENT),

	@HeadConfig(headId = "35861")
	WITCH(Sound.ENTITY_WITCH_AMBIENT),

	@HeadConfig(headId = "22399")
	WITHER(Sound.ENTITY_WITHER_AMBIENT),

	@HeadConfig(headType = Material.WITHER_SKELETON_SKULL)
	WITHER_SKELETON(Sound.ENTITY_WITHER_SKELETON_AMBIENT),

	@HeadConfig(headId = "38471")
	WOLF(Sound.ENTITY_WOLF_AMBIENT),

	@HeadConfig(headId = "35932")
	ZOGLIN(Sound.ENTITY_ZOGLIN_ANGRY),

	@HeadConfig(headType = Material.ZOMBIE_HEAD)
	ZOMBIE(Sound.ENTITY_ZOMBIE_AMBIENT),

	@HeadConfig(headId = "33747")
	ZOMBIE_HORSE(Sound.ENTITY_ZOMBIE_HORSE_AMBIENT),

	@HeadConfig(headId = "27600", variantClass = ZombieVillagerVariant.class)
	ZOMBIE_VILLAGER(
		entity -> ZombieVillagerVariant.of((ZombieVillager) entity),
		(entity, type) -> ((ZombieVillager) entity).setVillagerType((Villager.Type) type),
		() -> RandomUtils.randomElement(Villager.Type.class),
		Sound.ENTITY_ZOMBIE_VILLAGER_AMBIENT
	),

	@HeadConfig(headId = "37253")
	ZOMBIFIED_PIGLIN(Sound.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT),
	;

	private final String headId;
	private final Material headType;
	private final EntityType entityType;
	private final Class<? extends MobHeadVariant> variantClass;
	private final Function<Entity, MobHeadVariant> variantConverter;
	private final BiConsumer<Entity, Object> variantSetter;
	private final Supplier<?> randomVariant;
	private final ItemStack baseSkull;
	private final ItemStack namedSkull;
	private final Sound ambientSound;

	MobHeadType() {
		this(null, null, null, null);
	}

	MobHeadType(Sound ambientSound) {
		this(null, null, null, ambientSound);
	}

	MobHeadType(
		Function<Entity, MobHeadVariant> variantConverter,
		BiConsumer<Entity, Object> variantSetter,
		Supplier<?> randomVariant,
		Sound ambientSound
	) {
		this.headId = getHeadConfig().headId();
		this.headType = getHeadConfig().headType();
		this.entityType = EntityType.valueOf(name());
		this.variantClass = getHeadConfig().variantClass();
		this.variantConverter = variantConverter;
		this.variantSetter = variantSetter;
		this.randomVariant = randomVariant;
		this.ambientSound = ambientSound;

		if (Nullables.isNotNullOrEmpty(headId) || gg.projecteden.nexus.utils.Nullables.isNotNullOrAir(headType)) {
			this.baseSkull = gg.projecteden.nexus.utils.Nullables.isNullOrAir(headType) ? Nexus.getHeadAPI().getItemHead(headId) : new ItemStack(headType);
			this.namedSkull = new ItemBuilder(this.baseSkull).name("&e" + getDisplayName() + " Head").lore("&3Mob Head").build();
		} else {
			if (!"PLAYER".equals(name()))
				Nexus.warn("No generic skull for MobType " + StringUtils.camelCase(this) + " defined");
			this.baseSkull = null;
			this.namedSkull = null;
		}
	}

	private static final List<MobHead> allMobHeads = new ArrayList<>();

	public static List<MobHead> getAllMobHeads() {
		if (allMobHeads.isEmpty())
			for (MobHeadType type : MobHeadType.values())
				if (type.hasVariants())
					allMobHeads.addAll(type.getVariants());
				else
					allMobHeads.add(type);

		return allMobHeads;
	}

	private static final List<MobHead> vanillaHeads = new ArrayList<>();

	public static List<MobHead> getVanillaHeads() {
		if (vanillaHeads.isEmpty())
			for (MobHeadType type : MobHeadType.values())
				if (type.getHeadType() != null)
					vanillaHeads.add(type);

		return vanillaHeads;
	}

	public double getChance() {
		return new MobHeadChanceConfigService().get0().getChances().getOrDefault(this, 0d);
	}

	@Nullable
	public static MobHeadType of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from == entry.getEntityType()).findFirst().orElse(null);
	}

	@Nullable
	public static MobHeadType of(String id) {
		for (MobHeadType type : values()) {
			if (type.hasVariants()) {
				for (MobHeadVariant variant : type.getVariants()) {
					if (variant == null || variant.getHeadId() == null)
						continue;

					if (variant.getHeadId().equalsIgnoreCase(id))
						return type;
				}
			} else if (type.getHeadId().equalsIgnoreCase(id)) {
				return type;
			}
		}

		return null;
	}

	@Override
	public @NotNull MobHeadType getType() {
		return this;
	}


	public @Nullable ItemStack getNamedSkull() {
		if (namedSkull == null)
			return null;
		return namedSkull.clone();
	}

	public @Nullable ItemStack getBaseSkull() {
		if (baseSkull == null)
			return null;
		return baseSkull.clone();
	}

	public ItemStack getSkull(MobHeadVariant variant) {
		return variant == null ? namedSkull : variant.getNamedSkull();
	}

	public boolean hasVariants() {
		return getVariantClass() != null && getVariantClass() != MobHeadVariant.class;
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

	private static final List<EntityType> EXCLUDED_TYPES = List.of(EntityType.ARMOR_STAND, EntityType.GIANT, EntityType.NPC);

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

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public HeadConfig getHeadConfig() {
		return getField().getAnnotation(HeadConfig.class);
	}

}
