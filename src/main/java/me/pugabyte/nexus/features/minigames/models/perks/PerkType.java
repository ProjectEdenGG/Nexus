package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.pride21.Flags;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.BasicTrail;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.GreenTrail;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.RainbowTrail;
import me.pugabyte.nexus.features.minigames.perks.gadgets.DyeBombGadget;
import me.pugabyte.nexus.features.minigames.perks.gadgets.SnowballGadget;
import me.pugabyte.nexus.features.minigames.perks.gadgets.SpringGadget;
import me.pugabyte.nexus.features.minigames.perks.loadouts.CustomModelHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.FlagHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.HatMaterialImpl;
import me.pugabyte.nexus.features.minigames.perks.loadouts.LeavesHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.TeamHatMaterialImpl;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.BicornHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.BicornSideHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.CavalierHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.TricornHat;
import me.pugabyte.nexus.features.minigames.perks.particles.CloudParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.FlameParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.HeartParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.SoulFlameParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.SplashParticle;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public enum PerkType implements IHasPerkCategory {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle()),
	UNICORN_HORN(new HatMaterialImpl(Material.END_ROD, "Unicorn Horn", 25, "Become a pretty unicorn with this glowing horn on top of your head")),
	CONCRETE_HAT(new TeamHatMaterialImpl("Concrete", 30, "Protect your head with a slab of concrete!||&3Disclaimer: does not actually protect you.", colorType -> colorType.getConcrete())),
	BASIC_TRAIL(new BasicTrail()),
	SKELETON_SKULL(new HatMaterialImpl(Material.SKELETON_SKULL, 10, "Snipe your foes with the incredible prowess of a skeleton")),
	CREEPER_SKULL(new HatMaterialImpl(Material.CREEPER_HEAD, 10, "A disguise so scary your enemies will be saying \"aww man!\"")),
	BICORN_HAT(new BicornHat()),
	BICORN_SIDE_HAT(new BicornSideHat()),
	CAVALIER_HAT(new CavalierHat()),
	TRICORN_HAT(new TricornHat()),
	MARKSMANS_HAT(new CustomModelHat(Material.STONE_BUTTON, 21, "Marksman's Hat", 20, "Shoot your targets with uncanny accuracy with this hat")),
	GREEN_TRAIL(new GreenTrail()),
	RAINBOW_TRAIL(new RainbowTrail()),
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
	MUSHROOM(new HatMaterialImpl(Material.RED_MUSHROOM_BLOCK, "Mushroom Block", 10, "Mushroom Hat||&3Mushroom Hat||&3Whatever could it mean?")),
	NETHERITE(new HatMaterialImpl(Material.NETHERITE_BLOCK, 75, "Encase yourself in the most valuable block known to man")),
	HONEY(new HatMaterialImpl(Material.HONEY_BLOCK, 15, "Encase yourself in a delicious block of honey")),
	CLOUD(new CloudParticle()),
	SPLASH(new SplashParticle()),
	SOUL_FLAME(new SoulFlameParticle()),
	SNOWBALLS(new SnowballGadget()),
	SPRING(new SpringGadget()),
	DYE_BOMB(new DyeBombGadget()),
	TRAFFIC_CONE(new CustomModelHat(Material.ORANGE_CONCRETE, 3, "Traffic Cone", 25, "Warn others of ongoing construction with this flashy hat")),
	ACE_FLAG(new FlagHat(Flags.ACE)),
	AGENDER_FLAG(new FlagHat(Flags.AGENDER)),
	BI_FLAG(new FlagHat(Flags.BI)),
	GAY_FLAG(new FlagHat(Flags.GAY)),
	GENDERFLUID_FLAG(new FlagHat(Flags.GENDERFLUID)),
	LESBIAN_FLAG(new FlagHat(Flags.LESBIAN)),
	NONBINARY_FLAG(new FlagHat(Flags.NONBINARY)),
	PAN_FLAG(new FlagHat(Flags.PAN)),
	POLYAM_FLAG(new FlagHat(Flags.POLYAM)),
	TRANS_FLAG(new FlagHat(Flags.TRANS))
	;

	private final Perk perk;

	@Override
	public @NotNull PerkCategory getPerkCategory() {
		return perk.getPerkCategory();
	}

	public String getName() {
		return perk.getName();
	}

	public int getPrice() {
		return perk.getPrice();
	}
}
