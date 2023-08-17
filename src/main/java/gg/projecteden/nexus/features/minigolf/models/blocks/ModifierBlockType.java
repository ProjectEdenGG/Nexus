package gg.projecteden.nexus.features.minigolf.models.blocks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ModifierBlockType {
	// Blocks
	HOLE(new HoleBlock()),
	GRAVITY(new GravityBlock()),
	DEATH(new DeathBlock()),
	FRICTIONLESS(new FrictionlessBlock()),
	FRICTION(new FrictionBlock()),
	BOUNCE(new BounceBlock()),
	CATCH(new CatchBlock()),
	BOOST(new BoostBlock()),
	DIRECTIONAL_BOOST(new DirectionalBoostBlock()),
	CONVEYOR(new ConveyorBlock()),
	TELEPORT(new TeleportBlock()),
	CANNON(new CannonBlock()),
	TNT_BLOCK(new TNTBlock()),
	// Skulls
	TNT_SKULL(new TNTSkull()),
	//
	DEFAULT(new DefaultBlock()),
	;

	@Getter
	private final ModifierBlock modifierBlock;
}
