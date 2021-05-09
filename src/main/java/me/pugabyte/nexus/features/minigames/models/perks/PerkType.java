package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.BasicTrail;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.GreenTrail;
import me.pugabyte.nexus.features.minigames.perks.arrowparticles.RainbowTrail;
import me.pugabyte.nexus.features.minigames.perks.gadgets.DyeBombGadget;
import me.pugabyte.nexus.features.minigames.perks.gadgets.SnowballGadget;
import me.pugabyte.nexus.features.minigames.perks.gadgets.SpringGadget;
import me.pugabyte.nexus.features.minigames.perks.loadouts.*;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.ColorfulGlassHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.ConcreteHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.ConcretePowderHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.TerracottaHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.WoolHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.WoolScarf;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.BicornHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.BicornSideHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.CavalierHat;
import me.pugabyte.nexus.features.minigames.perks.loadouts.teamed.pirate.TricornHat;
import me.pugabyte.nexus.features.minigames.perks.particles.CloudParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.FlameParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.HeartParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.SoulFlameParticle;
import me.pugabyte.nexus.features.minigames.perks.particles.SplashParticle;

@AllArgsConstructor
@Getter
public enum PerkType implements IHasPerkCategory {
	FLAME_PARTICLE(new FlameParticle()),
	HEART_PARTICLE(new HeartParticle()),
	UNICORN_HORN(new UnicornHorn()),
	CONCRETE_HAT(new ConcreteHat()),
	BASIC_TRAIL(new BasicTrail()),
	SKELETON_SKULL(new SkeletonSkull()),
	CREEPER_SKULL(new CreeperSkull()),
	BICORN_HAT(new BicornHat()),
	BICORN_SIDE_HAT(new BicornSideHat()),
	CAVALIER_HAT(new CavalierHat()),
	TRICORN_HAT(new TricornHat()),
	MARKSMANS_HAT(new MarksmansHat()),
	GREEN_TRAIL(new GreenTrail()),
	RAINBOW_TRAIL(new RainbowTrail()),
	TERRACOTTA_HAT(new TerracottaHat()),
	WOOL_HAT(new WoolHat()),
	WOOL_SCARF(new WoolScarf()),
	COLORFUL_GLASS(new ColorfulGlassHat()),
	GLASS_HAT(new GlassHat()),
	CONCRETE_POWDER(new ConcretePowderHat()),
	GRASS_BLOCK(new GrassBlockHat()),
	OAK_LEAVES(new OakLeavesHat()),
	BIRCH_LEAVES(new BirchLeavesHat()),
	SPRUCE_LEAVES(new SpruceLeavesHat()),
	JUNGLE_LEAVES(new JungleLeavesHat()),
	ACACIA_LEAVES(new AcaciaLeavesHat()),
	DARK_OAK_LEAVES(new DarkOakLeavesHat()),
	DIAMOND_ORE(new DiamondOreHat()),
	NOTE_BLOCK(new NoteblockHat()),
	JACK_O_LANTERN(new JackOLanternHat()),
	ICE(new IceHat()),
	SEA_LANTERN(new SeaLanternHat()),
	SHROOMLIGHT(new ShroomlightHat()),
	MUSHROOM(new MushroomHat()),
	NETHERITE(new NetheriteHat()),
	HONEY(new HoneyHat()),
	CLOUD(new CloudParticle()),
	SPLASH(new SplashParticle()),
	SOUL_FLAME(new SoulFlameParticle()),
	SNOWBALLS(new SnowballGadget()),
	SPRING(new SpringGadget()),
	DYE_BOMB(new DyeBombGadget()),
	TRAFFIC_CONE(new TrafficConeHat())
	;

	private final Perk perk;

	@Override
	public PerkCategory getPerkCategory() {
		return perk.getPerkCategory();
	}

	public String getName() {
		return perk.getName();
	}

	public int getPrice() {
		return perk.getPrice();
	}
}
