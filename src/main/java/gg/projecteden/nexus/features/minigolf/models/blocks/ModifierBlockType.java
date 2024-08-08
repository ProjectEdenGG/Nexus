package gg.projecteden.nexus.features.minigolf.models.blocks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum ModifierBlockType {
	// Blocks
	HOLE(new HoleBlock()),
	GRAVITY(new GravityBlock()),
	DEATH(new DeathBlock()),
	FRICTIONLESS(new FrictionlessBlock()),
	FRICTION(new FrictionBlock()),
	BOUNCE_BLOCK(new BounceBlock()),
	CATCH(new CatchBlock()),
	BOOST(new BoostBlock()),
	DIRECTIONAL_BOOST(new DirectionalBoostBlock()),
	CONVEYOR(new ConveyorBlock()),
	TELEPORT(new TeleportBlock()),
	CANNON(new CannonBlock()),
	TNT_BLOCK(new TNTBlock()),
	// Skulls
	BOUNCE_SKULL(new BounceSkull()),
	TNT_SKULL(new TNTSkull()),
	BUMPER_SKULL(new BumperSkull()),

	// Default - Must be last
	DEFAULT(new DefaultBlock()),
	;

	private final ModifierBlock modifierBlock;

	public Set<Material> getMaterials() {
		return modifierBlock.getMaterials();
	}
}
