package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.mechanics.*;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

@Getter
public enum MechanicType {
	@Group(MechanicGroup.ARCADE)							ARCHERY(Archery.class),
	@Group(MechanicGroup.ARCADE)							BATTLESHIP(Battleship.class),
	@Group(MechanicGroup.ARCADE)							DROPPER(Dropper.class),
	@Group(MechanicGroup.ARCADE)							FALLING_BLOCKS(FallingBlocks.class),
	@Group(MechanicGroup.ARCADE)							GRAB_A_JUMBUCK(GrabAJumbuck.class),
	@Group(MechanicGroup.ARCADE)							HIDE_AND_SEEK(HideAndSeek.class),
	@Group(MechanicGroup.ARCADE)							HOLE_IN_THE_WALL(HoleInTheWall.class),
	@Group(MechanicGroup.ARCADE)							KANGAROO_JUMPING(KangarooJumping.class),
	@Group(MechanicGroup.ARCADE)							MASTERMIND(Mastermind.class),
	@Group(MechanicGroup.ARCADE)							MONSTER_MAZE(MonsterMaze.class),
	@Group(MechanicGroup.ARCADE)							PIXEL_DROP(PixelDrop.class),
	@Group(MechanicGroup.ARCADE)							PIXEL_PAINTERS(PixelPainters.class),
	@Group(MechanicGroup.ARCADE)							SABOTAGE(Sabotage.class),
	@Group(MechanicGroup.ARCADE)							THIMBLE(Thimble.class),
	@Group(MechanicGroup.ARCADE)							TNT_RUN(TNTRun.class),

	@Group(MechanicGroup.PVP)								CAPTURE_THE_FLAG(CaptureTheFlag.class),
	@Group(MechanicGroup.PVP)								DOMINATION(Domination.class),
	@Group(MechanicGroup.PVP) @Parent(CaptureTheFlag.class) FLAG_RUSH(FlagRush.class),
	@Group(MechanicGroup.PVP) @Parent(TeamDeathmatch.class) FOUR_TEAM_DEATHMATCH(FourTeamDeathmatch.class),
	@Group(MechanicGroup.PVP)								FREE_FOR_ALL(FreeForAll.class),
	@Group(MechanicGroup.PVP)								GOLD_RUSH(GoldRush.class),
	@Group(MechanicGroup.PVP)								INFECTION(Infection.class),
	@Group(MechanicGroup.PVP)								JUGGERNAUT(Juggernaut.class),
	@Group(MechanicGroup.PVP)								KING_OF_THE_HILL(KingOfTheHill.class),
	@Group(MechanicGroup.PVP)								ONE_IN_THE_QUIVER(OneInTheQuiver.class),
	@Group(MechanicGroup.PVP)								ONE_VS_ONE(OneVsOne.class),
	@Group(MechanicGroup.PVP)								SIEGE(Siege.class),
	@Group(MechanicGroup.PVP)								TEAM_DEATHMATCH(TeamDeathmatch.class),
	@Group(MechanicGroup.PVP)								TURF_WARS(TurfWars.class),

	@Group(MechanicGroup.SURVIVAL_ROYALE)					BINGO(Bingo.class),
	@Group(MechanicGroup.SURVIVAL_ROYALE)					DEATH_SWAP(DeathSwap.class),
	@Group(MechanicGroup.SURVIVAL_ROYALE)					UHC(UHC.class),
	@Group(MechanicGroup.SURVIVAL_ROYALE)					UNCIVIL_ENGINEERS(UncivilEngineers.class),

	@Group(MechanicGroup.TIMED)								MAZE(Maze.class),
	@Group(MechanicGroup.TIMED)								PARKOUR(Parkour.class),
	@Group(MechanicGroup.TIMED) @Parent(Parkour.class)		XRUN(XRun.class),

	@Group(MechanicGroup.TRADITIONAL) @Parent(Quake.class)	DOGFIGHTING(Dogfighting.class),
	@Group(MechanicGroup.TRADITIONAL)						MURDER(Murder.class),
	@Group(MechanicGroup.TRADITIONAL)						PAINTBALL(Paintball.class),
	@Group(MechanicGroup.TRADITIONAL)						QUAKE(Quake.class),
	@Group(MechanicGroup.TRADITIONAL)						SPLEEF(Spleef.class),
	@Group(MechanicGroup.TRADITIONAL) @Parent(Spleef.class)	SPLEGG(Splegg.class),
	;

	MechanicType(Class<? extends Mechanic> mechanic) {
		this.mechanic = Nexus.singletonOf(mechanic);
	}

	private final @NotNull Mechanic mechanic;

	public @NotNull Mechanic get() {
		return mechanic;
	}

	public static MechanicType of(Class<? extends Mechanic> mechanic) {
		for (MechanicType type : values())
			if (type.getMechanic().getClass() == mechanic)
				return type;

		return null;
	}

	private static final String BOUNDING_BOX_ID_PREFIX = "minigames_lobby_mechanic_";

	public static MechanicType from(CustomBoundingBoxEntity entity) {
		String id = entity.getId();
		if (!id.startsWith(BOUNDING_BOX_ID_PREFIX))
			throw new InvalidInputException("Mechanic ImageStand does not have expected prefix (found " + id + ", expected " + BOUNDING_BOX_ID_PREFIX + ")");

		final String mechanicName = id.replace(BOUNDING_BOX_ID_PREFIX, "");

		try {
			return MechanicType.valueOf(mechanicName.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("&cMechanic &e" + mechanicName + " &cnot found");
		}
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public MechanicGroup getGroup() {
		return getField().getAnnotation(Group.class).value();
	}

	public MechanicType getParent() {
		return of(getField().getAnnotation(Parent.class).value());
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Group {
		MechanicGroup value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Parent {
		Class<? extends Mechanic> value();
	}

}
