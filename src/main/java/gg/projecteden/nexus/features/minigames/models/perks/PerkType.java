package gg.projecteden.nexus.features.minigames.models.perks;

import gg.projecteden.api.interfaces.Named;
import gg.projecteden.nexus.features.events.y2021.pride21.Flags;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.BasicTrail;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.BubbleTrail;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.FlameTrail;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.GreenTrail;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.PETRail;
import gg.projecteden.nexus.features.minigames.perks.arrowparticles.RainbowTrail;
import gg.projecteden.nexus.features.minigames.perks.gadgets.DyeBombGadget;
import gg.projecteden.nexus.features.minigames.perks.gadgets.SnowballGadget;
import gg.projecteden.nexus.features.minigames.perks.gadgets.SpringGadget;
import gg.projecteden.nexus.features.minigames.perks.loadouts.CustomModelHat;
import gg.projecteden.nexus.features.minigames.perks.loadouts.FlagHat;
import gg.projecteden.nexus.features.minigames.perks.loadouts.HatMaterialImpl;
import gg.projecteden.nexus.features.minigames.perks.loadouts.LeavesHat;
import gg.projecteden.nexus.features.minigames.perks.loadouts.teamed.DyeableCustomModelHat;
import gg.projecteden.nexus.features.minigames.perks.loadouts.teamed.TeamHatMaterialImpl;
import gg.projecteden.nexus.features.minigames.perks.particles.CloudParticle;
import gg.projecteden.nexus.features.minigames.perks.particles.FlameParticle;
import gg.projecteden.nexus.features.minigames.perks.particles.HeartParticle;
import gg.projecteden.nexus.features.minigames.perks.particles.SoulFlameParticle;
import gg.projecteden.nexus.features.minigames.perks.particles.SplashParticle;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum PerkType implements IHasPerkCategory, Named {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle()),
	UNICORN_HORN(new HatMaterialImpl(Material.END_ROD, "Unicorn Horn", 25, "Become a pretty unicorn with this glowing horn on top of your head")),
	CONCRETE_HAT(new TeamHatMaterialImpl("Concrete", 30, List.of("Protect your head with a slab of concrete!", "&3Disclaimer: does not actually protect you."), colorType -> colorType.getConcrete())),
	BASIC_TRAIL(new BasicTrail()),
	SKELETON_SKULL(new HatMaterialImpl(Material.SKELETON_SKULL, 10, "Snipe your foes with the incredible prowess of a skeleton")),
	CREEPER_SKULL(new HatMaterialImpl(Material.CREEPER_HEAD, 10, "A disguise so scary your enemies will be saying \"aww man!\"")),
	BICORN_HAT(DyeableCustomModelHat.createPirateHat("Bicorn", 4001)),
	BICORN_SIDE_HAT(DyeableCustomModelHat.createPirateHat("Bicorn Side", 4002)),
	CAVALIER_HAT(DyeableCustomModelHat.createPirateHat("Cavalier", 4000)),
	TRICORN_HAT(DyeableCustomModelHat.createPirateHat("Tricorn", 4003)),
	MARKSMANS_HAT(new CustomModelHat(CustomMaterial.COSTUMES_PIRATE_HAT_LEATHER_CAVALIER, "Marksman's Hat", 20, "Shoot your targets with uncanny accuracy with this hat")),
	GREEN_TRAIL(new GreenTrail()),
	RAINBOW_TRAIL(new RainbowTrail()),
	FLAME_TRAIL(new FlameTrail()),
	BUBBLE_TRAIL(new BubbleTrail()),
	PE_TRAIL(new PETRail()),
	TERRACOTTA_HAT(new TeamHatMaterialImpl("Terracotta", 25, "Protect your head with this uniquely colored clay", colorType -> colorType.getTerracotta())),
	WOOL_HAT(new TeamHatMaterialImpl("Wool Hood", 25, "Keep yourself extra warm with this wool covering your head", colorType -> colorType.getWool())),
	WOOL_SCARF(new TeamHatMaterialImpl("Wool Scarf", 25, "Keep yourself warm and cozy with this wool scarf", colorType -> colorType.getCarpet())),
	COLORFUL_GLASS(new TeamHatMaterialImpl("Stained Glass", 35, "Become a colorful astronaut with this stained glass hat", colorType -> colorType.getStainedGlass())),
	GLASS_HAT(new HatMaterialImpl(Material.GLASS, 15, "Become an astronaut with this shiny glass hat")),
	CONCRETE_POWDER(new TeamHatMaterialImpl("Concrete Powder", 25, "Encase your head in a cube of concrete powder", colorType -> colorType.getConcretePowder())),
	GRASS_BLOCK(new HatMaterialImpl(Material.GRASS_BLOCK, 1, "The most basic element of life on our planet")),
	OAK_LEAVES(new LeavesHat(Material.OAK_LEAVES)),
	BIRCH_LEAVES(new LeavesHat(Material.BIRCH_LEAVES)),
	SPRUCE_LEAVES(new LeavesHat(Material.SPRUCE_LEAVES)),
	JUNGLE_LEAVES(new LeavesHat(Material.JUNGLE_LEAVES)),
	ACACIA_LEAVES(new LeavesHat(Material.ACACIA_LEAVES)),
	DARK_OAK_LEAVES(new LeavesHat(Material.DARK_OAK_LEAVES)),
	DIAMOND_ORE(new HatMaterialImpl(Material.DIAMOND_ORE, 25, "The most precious element of our overworld")),
	NOTE_BLOCK(new HatMaterialImpl(Material.NOTE_BLOCK, 10, "Sing a nice tune with this note block hat")),
	JACK_O_LANTERN(new HatMaterialImpl(Material.JACK_O_LANTERN, 10, "Scare your friends with this scarecrow head")),
	ICE(new HatMaterialImpl(Material.ICE, "Ice Cube", 15, "Keep yourself cool in the summer with this cube of ice")),
	SEA_LANTERN(new HatMaterialImpl(Material.SEA_LANTERN, 20, "Illuminate like the lights of something under the sea")),
	SHROOMLIGHT(new HatMaterialImpl(Material.SHROOMLIGHT, 20, "Illuminate like the lights of something out of this world")),
	MUSHROOM(new HatMaterialImpl(Material.RED_MUSHROOM_BLOCK, "Mushroom Block", 10, List.of("Mushroom Hat", "&3Mushroom Hat", "&3Whatever could it mean?"))),
	NETHERITE(new HatMaterialImpl(Material.NETHERITE_BLOCK, 75, "Encase yourself in the most valuable block known to man")),
	HONEY(new HatMaterialImpl(Material.HONEY_BLOCK, 15, "Encase yourself in a delicious block of honey")),
	CLOUD(new CloudParticle()),
	SPLASH(new SplashParticle()),
	SOUL_FLAME(new SoulFlameParticle()),
	SNOWBALLS(new SnowballGadget()),
	SPRING(new SpringGadget()),
	DYE_BOMB(new DyeBombGadget()),
	TRAFFIC_CONE(new CustomModelHat(CustomMaterial.LEGACY_TRAFFIC_CONE, "Traffic Cone", 25, "Warn others of ongoing construction with this flashy hat")),
	ACE_FLAG(new FlagHat(Flags.ACE)),
	AGENDER_FLAG(new FlagHat(Flags.AGENDER)),
	ARO_FLAG(new FlagHat(Flags.ARO)),
	BI_FLAG(new FlagHat(Flags.BI)),
	DEMI_FLAG(new FlagHat(Flags.DEMI)),
	DEMIBOY_FLAG(new FlagHat(Flags.DEMIBOY)),
	DEMIGIRL_FLAG(new FlagHat(Flags.DEMIGIRL)),
	DEMIROMANTIC_FLAG(new FlagHat(Flags.DEMIROMANTIC)),
	GAY_FLAG(new FlagHat(Flags.GAY)),
	GENDERFLUID_FLAG(new FlagHat(Flags.GENDERFLUID)),
	GENDERFLUX_FLAG(new FlagHat(Flags.GENDERFLUX)),
	GENDERQUEER_FLAG(new FlagHat(Flags.GENDERQUEER)),
	GRAY_ACE_FLAG(new FlagHat(Flags.GRAY_ACE)),
	GRAY_ARO_FLAG(new FlagHat(Flags.GRAY_ARO)),
	INTERSEX_FLAG(new FlagHat(Flags.INTERSEX)),
	LESBIAN_FLAG(new FlagHat(Flags.LESBIAN)),
	NONBINARY_FLAG(new FlagHat(Flags.NONBINARY)),
	PAN_FLAG(new FlagHat(Flags.PAN)),
	POLYAM_FLAG(new FlagHat(Flags.POLYAM)),
	POLYSEX_FLAG(new FlagHat(Flags.POLYSEX)),
	TRANS_FLAG(new FlagHat(Flags.TRANS)),
	TRANSFEM_FLAG(new FlagHat(Flags.TRANSFEM)),
	TRANSMASC_FLAG(new FlagHat(Flags.TRANSMASC)),
	QUEER_FLAG(new FlagHat(Flags.QUEER)),
	;

	private final Perk perk;

	@Override
	public @NotNull PerkCategory getPerkCategory() {
		return perk.getPerkCategory();
	}

	public @NotNull String getName() {
		return perk.getName();
	}

	public int getPrice() {
		return perk.getPrice();
	}

	public static Set<PerkType> getByCategory(PerkCategory category) {
		return Arrays.stream(PerkType.values()).filter(perkType -> perkType.getPerkCategory() == category).collect(Collectors.toSet());
	}
}
