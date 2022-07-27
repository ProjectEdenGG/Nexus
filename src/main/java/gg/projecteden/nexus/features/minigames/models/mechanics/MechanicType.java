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

import static gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup.ARCADE;
import static gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup.PVP;
import static gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup.SURVIVAL_ROYALE;
import static gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup.TIMED;
import static gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup.TRADITIONAL;

@Getter
public enum MechanicType {
	@Group(ARCADE)
	ARCHERY(Archery.class),

	@Group(ARCADE)
	BATTLESHIP(Battleship.class),

	@Group(ARCADE)
	FALLING_BLOCKS(FallingBlocks.class),

	@Group(ARCADE)
	GRAB_A_JUMBUCK(GrabAJumbuck.class),

	@Group(ARCADE)
	HIDE_AND_SEEK(HideAndSeek.class),

	@Group(ARCADE)
	HOLE_IN_THE_WALL(HoleInTheWall.class),

	@Group(ARCADE)
	KANGAROO_JUMPING(KangarooJumping.class),

	@Group(ARCADE)
	MASTERMIND(Mastermind.class),

	@Group(ARCADE)
	MONSTER_MAZE(MonsterMaze.class),

	@Group(ARCADE)
	PIXEL_DROP(PixelDrop.class),

	@Group(ARCADE)
	PIXEL_PAINTERS(PixelPainters.class),

	@Group(ARCADE)
	SABOTAGE(Sabotage.class),

	@Group(ARCADE)
	THIMBLE(Thimble.class),

	@Group(ARCADE)
	TNT_RUN(TNTRun.class),

	@Group(PVP)
	CAPTURE_THE_FLAG(CaptureTheFlag.class),

	@Group(PVP)
	@Parent(TEAM_DEATHMATCH)
	FOUR_TEAM_DEATHMATCH(FourTeamDeathmatch.class),

	@Group(PVP)
	FREE_FOR_ALL(FreeForAll.class),

	@Group(PVP)
	GOLD_RUSH(GoldRush.class),

	@Group(PVP)
	INFECTION(Infection.class),

	@Group(PVP)
	JUGGERNAUT(Juggernaut.class),

	@Group(PVP)
	@Parent(CAPTURE_THE_FLAG)
	FLAG_RUSH(FlagRush.class),

	@Group(PVP)
	ONE_IN_THE_QUIVER(OneInTheQuiver.class),

	@Group(PVP)
	SIEGE(Siege.class),

	@Group(PVP)
	TEAM_DEATHMATCH(TeamDeathmatch.class),

	@Group(TRADITIONAL)
	DOGFIGHTING(Dogfighting.class),

	@Group(TRADITIONAL)
	MURDER(Murder.class),

	@Group(TRADITIONAL)
	PAINTBALL(Paintball.class),

	@Group(TRADITIONAL)
	QUAKE(Quake.class),

	@Group(TRADITIONAL)
	SPLEEF(Spleef.class),

	@Group(TRADITIONAL)
	@Parent(SPLEEF)
	SPLEGG(Splegg.class),

	@Group(TIMED)
	MAZE(Maze.class),

	@Group(TIMED)
	PARKOUR(Parkour.class),

	@Group(TIMED)
	@Parent(PARKOUR)
	XRUN(XRun.class),

	@Group(SURVIVAL_ROYALE)
	BINGO(Bingo.class),

	@Group(SURVIVAL_ROYALE)
	DEATH_SWAP(DeathSwap.class),

	@Group(SURVIVAL_ROYALE)
	UHC(UHC.class),

	@Group(SURVIVAL_ROYALE)
	UNCIVIL_ENGINEERS(UncivilEngineers.class),
	;

	MechanicType(Class<? extends Mechanic> mechanic) {
		this.mechanic = Nexus.singletonOf(mechanic);
	}

	private final @NotNull Mechanic mechanic;

	public @NotNull Mechanic get() {
		return mechanic;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public MechanicGroup getGroup() {
		return getField().getAnnotation(Group.class).value();
	}

	public MechanicType getParent() {
		return getField().getAnnotation(Parent.class).value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Group {
		MechanicGroup value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Parent {
		MechanicType value();
	}

}
