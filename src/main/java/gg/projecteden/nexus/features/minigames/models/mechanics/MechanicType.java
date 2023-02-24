package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.mechanics.Archery;
import gg.projecteden.nexus.features.minigames.mechanics.Battleship;
import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.mechanics.CaptureTheFlag;
import gg.projecteden.nexus.features.minigames.mechanics.DeathSwap;
import gg.projecteden.nexus.features.minigames.mechanics.Dogfighting;
import gg.projecteden.nexus.features.minigames.mechanics.FallingBlocks;
import gg.projecteden.nexus.features.minigames.mechanics.FlagRush;
import gg.projecteden.nexus.features.minigames.mechanics.FourTeamDeathmatch;
import gg.projecteden.nexus.features.minigames.mechanics.FreeForAll;
import gg.projecteden.nexus.features.minigames.mechanics.GoldRush;
import gg.projecteden.nexus.features.minigames.mechanics.GrabAJumbuck;
import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.features.minigames.mechanics.HoleInTheWall;
import gg.projecteden.nexus.features.minigames.mechanics.Infection;
import gg.projecteden.nexus.features.minigames.mechanics.Juggernaut;
import gg.projecteden.nexus.features.minigames.mechanics.KangarooJumping;
import gg.projecteden.nexus.features.minigames.mechanics.Mastermind;
import gg.projecteden.nexus.features.minigames.mechanics.Maze;
import gg.projecteden.nexus.features.minigames.mechanics.MonsterMaze;
import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.mechanics.OneInTheQuiver;
import gg.projecteden.nexus.features.minigames.mechanics.OneVsOne;
import gg.projecteden.nexus.features.minigames.mechanics.Paintball;
import gg.projecteden.nexus.features.minigames.mechanics.Parkour;
import gg.projecteden.nexus.features.minigames.mechanics.PixelDrop;
import gg.projecteden.nexus.features.minigames.mechanics.PixelPainters;
import gg.projecteden.nexus.features.minigames.mechanics.Quake;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.mechanics.Siege;
import gg.projecteden.nexus.features.minigames.mechanics.Spleef;
import gg.projecteden.nexus.features.minigames.mechanics.Splegg;
import gg.projecteden.nexus.features.minigames.mechanics.TNTRun;
import gg.projecteden.nexus.features.minigames.mechanics.TeamDeathmatch;
import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.mechanics.UHC;
import gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers;
import gg.projecteden.nexus.features.minigames.mechanics.XRun;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

@Getter
public enum MechanicType {
	@Group(MechanicGroup.ARCADE)
	ARCHERY(Archery.class),

	@Group(MechanicGroup.ARCADE)
	BATTLESHIP(Battleship.class),

	@Group(MechanicGroup.ARCADE)
	FALLING_BLOCKS(FallingBlocks.class),

	@Group(MechanicGroup.ARCADE)
	GRAB_A_JUMBUCK(GrabAJumbuck.class),

	@Group(MechanicGroup.ARCADE)
	HIDE_AND_SEEK(HideAndSeek.class),

	@Group(MechanicGroup.ARCADE)
	HOLE_IN_THE_WALL(HoleInTheWall.class),

	@Group(MechanicGroup.ARCADE)
	KANGAROO_JUMPING(KangarooJumping.class),

	@Group(MechanicGroup.ARCADE)
	MASTERMIND(Mastermind.class),

	@Group(MechanicGroup.ARCADE)
	MONSTER_MAZE(MonsterMaze.class),

	@Group(MechanicGroup.ARCADE)
	PIXEL_DROP(PixelDrop.class),

	@Group(MechanicGroup.ARCADE)
	PIXEL_PAINTERS(PixelPainters.class),

	@Group(MechanicGroup.ARCADE)
	SABOTAGE(Sabotage.class),

	@Group(MechanicGroup.ARCADE)
	THIMBLE(Thimble.class),

	@Group(MechanicGroup.ARCADE)
	TNT_RUN(TNTRun.class),

	@Group(MechanicGroup.PVP)
	CAPTURE_THE_FLAG(CaptureTheFlag.class),

	@Group(MechanicGroup.PVP)
	@Parent(TeamDeathmatch.class)
	FOUR_TEAM_DEATHMATCH(FourTeamDeathmatch.class),

	@Group(MechanicGroup.PVP)
	FREE_FOR_ALL(FreeForAll.class),

	@Group(MechanicGroup.PVP)
	GOLD_RUSH(GoldRush.class),

	@Group(MechanicGroup.PVP)
	INFECTION(Infection.class),

	@Group(MechanicGroup.PVP)
	JUGGERNAUT(Juggernaut.class),

	@Group(MechanicGroup.PVP)
	@Parent(CaptureTheFlag.class)
	FLAG_RUSH(FlagRush.class),

	@Group(MechanicGroup.PVP)
	ONE_IN_THE_QUIVER(OneInTheQuiver.class),

	@Group(MechanicGroup.PVP)
	SIEGE(Siege.class),

	@Group(MechanicGroup.PVP)
	TEAM_DEATHMATCH(TeamDeathmatch.class),

	@Group(MechanicGroup.TRADITIONAL)
	DOGFIGHTING(Dogfighting.class),

	@Group(MechanicGroup.TRADITIONAL)
	MURDER(Murder.class),

	@Group(MechanicGroup.PVP)
	ONE_VS_ONE(OneVsOne.class),

	@Group(MechanicGroup.TRADITIONAL)
	PAINTBALL(Paintball.class),

	@Group(MechanicGroup.TRADITIONAL)
	QUAKE(Quake.class),

	@Group(MechanicGroup.TRADITIONAL)
	SPLEEF(Spleef.class),

	@Group(MechanicGroup.TRADITIONAL)
	@Parent(Spleef.class)
	SPLEGG(Splegg.class),

	@Group(MechanicGroup.TIMED)
	MAZE(Maze.class),

	@Group(MechanicGroup.TIMED)
	PARKOUR(Parkour.class),

	@Group(MechanicGroup.TIMED)
	@Parent(Parkour.class)
	XRUN(XRun.class),

	@Group(MechanicGroup.SURVIVAL_ROYALE)
	BINGO(Bingo.class),

	@Group(MechanicGroup.SURVIVAL_ROYALE)
	DEATH_SWAP(DeathSwap.class),

	@Group(MechanicGroup.SURVIVAL_ROYALE)
	UHC(UHC.class),

	@Group(MechanicGroup.SURVIVAL_ROYALE)
	UNCIVIL_ENGINEERS(UncivilEngineers.class),
	;

	MechanicType(Class<? extends Mechanic> mechanic) {
		this.mechanic = Nexus.singletonOf(mechanic);
	}

	private final @NotNull Mechanic mechanic;

	public @NotNull Mechanic get() {
		return mechanic;
	}

	public MechanicType of(Class<? extends Mechanic> mechanic) {
		for (MechanicType type : values())
			if (type.getMechanic().getClass() == mechanic)
				return type;

		return null;
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
