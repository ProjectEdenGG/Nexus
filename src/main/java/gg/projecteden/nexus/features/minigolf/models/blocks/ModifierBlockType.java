package gg.projecteden.nexus.features.minigolf.models.blocks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ModifierBlockType {
	HOLE(new HoleBlock()),
	GRAVITY(new GravityBlock()),
	DEATH(new DeathBlock()),
	FRICTIONLESS(new FrictionlessBlock()),
	FRICTION(new FrictionBlock()),
	BOUNCE_BLOCK(new BounceBlock()),
	//BOUNCE_SKULL(new BounceSkull()),
	CATCH(new CatchBlock()),
	BOOST(new BoostBlock()),
	DIRECTIONAL_BOOST(new DirectionalBoostBlock()),
	CONVEYOR(new ConveyorBlock()),
	TELEPORT(new TeleportBlock()),
	CANNON(new CannonBlock()),
	DEFAULT(new DefaultBlock()),
	;

	@Getter
	private final ModifierBlock modifierBlock;
}
