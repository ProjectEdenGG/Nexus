package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Archery;
import gg.projecteden.nexus.features.minigames.mechanics.Battleship;
import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.mechanics.BlockParty;
import gg.projecteden.nexus.features.minigames.mechanics.CaptureTheFlag;
import gg.projecteden.nexus.features.minigames.mechanics.Checkers;
import gg.projecteden.nexus.features.minigames.mechanics.Connect4;
import gg.projecteden.nexus.features.minigames.mechanics.DeathSwap;
import gg.projecteden.nexus.features.minigames.mechanics.Dogfighting;
import gg.projecteden.nexus.features.minigames.mechanics.Domination;
import gg.projecteden.nexus.features.minigames.mechanics.Dropper;
import gg.projecteden.nexus.features.minigames.mechanics.ElytraRacing;
import gg.projecteden.nexus.features.minigames.mechanics.FallingBlocks;
import gg.projecteden.nexus.features.minigames.mechanics.FlagRush;
import gg.projecteden.nexus.features.minigames.mechanics.FourTeamDeathmatch;
import gg.projecteden.nexus.features.minigames.mechanics.FreeForAll;
import gg.projecteden.nexus.features.minigames.mechanics.GoldRush;
import gg.projecteden.nexus.features.minigames.mechanics.GrabAJumbuck;
import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.features.minigames.mechanics.HoleInTheWall;
import gg.projecteden.nexus.features.minigames.mechanics.HungerGames;
import gg.projecteden.nexus.features.minigames.mechanics.IceRally;
import gg.projecteden.nexus.features.minigames.mechanics.Infection;
import gg.projecteden.nexus.features.minigames.mechanics.Juggernaut;
import gg.projecteden.nexus.features.minigames.mechanics.KangarooJumping;
import gg.projecteden.nexus.features.minigames.mechanics.KingOfTheHill;
import gg.projecteden.nexus.features.minigames.mechanics.Mastermind;
import gg.projecteden.nexus.features.minigames.mechanics.Maze;
import gg.projecteden.nexus.features.minigames.mechanics.Megamind;
import gg.projecteden.nexus.features.minigames.mechanics.MonsterMaze;
import gg.projecteden.nexus.features.minigames.mechanics.Multimind;
import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.mechanics.OneInTheQuiver;
import gg.projecteden.nexus.features.minigames.mechanics.OneVsOne;
import gg.projecteden.nexus.features.minigames.mechanics.Paintball;
import gg.projecteden.nexus.features.minigames.mechanics.Parkour;
import gg.projecteden.nexus.features.minigames.mechanics.PixelDrop;
import gg.projecteden.nexus.features.minigames.mechanics.PixelPainters;
import gg.projecteden.nexus.features.minigames.mechanics.Quake;
import gg.projecteden.nexus.features.minigames.mechanics.Siege;
import gg.projecteden.nexus.features.minigames.mechanics.Spleef;
import gg.projecteden.nexus.features.minigames.mechanics.Splegg;
import gg.projecteden.nexus.features.minigames.mechanics.TNTRun;
import gg.projecteden.nexus.features.minigames.mechanics.TNTTag;
import gg.projecteden.nexus.features.minigames.mechanics.TeamDeathmatch;
import gg.projecteden.nexus.features.minigames.mechanics.Thimble;
import gg.projecteden.nexus.features.minigames.mechanics.TicTacToe;
import gg.projecteden.nexus.features.minigames.mechanics.TurfWars;
import gg.projecteden.nexus.features.minigames.mechanics.UHC;
import gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers;
import gg.projecteden.nexus.features.minigames.mechanics.XRun;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;

@Getter
public enum MechanicType {
	@Group(MechanicGroup.ARCADE)							ARCHERY(Archery.class),
	@Group(MechanicGroup.ARCADE)							BATTLESHIP(Battleship.class),
	@Group(MechanicGroup.ARCADE)							BLOCK_PARTY(BlockParty.class),
	@Group(MechanicGroup.ARCADE)							CHECKERS(Checkers.class),
	@Group(MechanicGroup.ARCADE)							CONNECT4(Connect4.class),
	@Group(MechanicGroup.ARCADE)							DROPPER(Dropper.class),
	@Group(MechanicGroup.ARCADE)							FALLING_BLOCKS(FallingBlocks.class),
	@Group(MechanicGroup.ARCADE)							GOLD_RUSH(GoldRush.class),
	@Group(MechanicGroup.ARCADE)							GRAB_A_JUMBUCK(GrabAJumbuck.class),
	@Group(MechanicGroup.ARCADE)							HOLE_IN_THE_WALL(HoleInTheWall.class),
	@Group(MechanicGroup.ARCADE)							KANGAROO_JUMPING(KangarooJumping.class),
	@Group(MechanicGroup.ARCADE)							MASTERMIND(Mastermind.class),
	@Group(MechanicGroup.ARCADE) /*@Parent(Mastermind.class)*/ MEGAMIND(Megamind.class),
	@Group(MechanicGroup.ARCADE) /*@Parent(Mastermind.class)*/ MULTIMIND(Multimind.class),
	@Group(MechanicGroup.ARCADE)							MONSTER_MAZE(MonsterMaze.class),
	@Group(MechanicGroup.ARCADE)							PIXEL_DROP(PixelDrop.class),
	@Group(MechanicGroup.ARCADE)							PIXEL_PAINTERS(PixelPainters.class),
	@Group(MechanicGroup.ARCADE)							THIMBLE(Thimble.class),
	@Group(MechanicGroup.ARCADE)							TICTACTOE(TicTacToe.class),
	@Group(MechanicGroup.ARCADE)							TNT_RUN(TNTRun.class),
	@Group(MechanicGroup.ARCADE)							TNT_TAG(TNTTag.class),

	@Group(MechanicGroup.PVP)								ONE_VS_ONE(OneVsOne.class),
	@Group(MechanicGroup.PVP)								CAPTURE_THE_FLAG(CaptureTheFlag.class),
	@Group(MechanicGroup.PVP)								DOMINATION(Domination.class),
	@Group(MechanicGroup.PVP) @Parent(CaptureTheFlag.class) FLAG_RUSH(FlagRush.class),
	@Group(MechanicGroup.PVP) @Parent(TeamDeathmatch.class) FOUR_TEAM_DEATHMATCH(FourTeamDeathmatch.class),
	@Group(MechanicGroup.PVP)								FREE_FOR_ALL(FreeForAll.class),
	@Group(MechanicGroup.PVP)								INFECTION(Infection.class),
	@Group(MechanicGroup.PVP)								JUGGERNAUT(Juggernaut.class),
	@Group(MechanicGroup.PVP)								KING_OF_THE_HILL(KingOfTheHill.class),
	@Group(MechanicGroup.PVP)								ONE_IN_THE_QUIVER(OneInTheQuiver.class),
	@Group(MechanicGroup.PVP)								SIEGE(Siege.class),
	@Group(MechanicGroup.PVP)								TEAM_DEATHMATCH(TeamDeathmatch.class),
	@Group(MechanicGroup.PVP)								TURF_WARS(TurfWars.class),

	@Group(MechanicGroup.SURVIVAL)							BINGO(Bingo.class),
	@Group(MechanicGroup.SURVIVAL)							DEATH_SWAP(DeathSwap.class),
	@Group(MechanicGroup.SURVIVAL)							HUNGER_GAMES(HungerGames.class),
	@Group(MechanicGroup.SURVIVAL)							UHC(UHC.class),
	@Group(MechanicGroup.SURVIVAL)							UNCIVIL_ENGINEERS(UncivilEngineers.class),

	@Group(MechanicGroup.TIMED)								ELYTRA_RACING(ElytraRacing.class),
	@Group(MechanicGroup.TIMED)								ICE_RALLY(IceRally.class),
	@Group(MechanicGroup.TIMED)								MAZE(Maze.class),
	@Group(MechanicGroup.TIMED)								PARKOUR(Parkour.class),
	@Group(MechanicGroup.TIMED) @Parent(Parkour.class)		XRUN(XRun.class),

	@Group(MechanicGroup.CLASSIC) @Parent(Quake.class)		DOGFIGHTING(Dogfighting.class),
	@Group(MechanicGroup.CLASSIC)							HIDE_AND_SEEK(HideAndSeek.class),
	@Group(MechanicGroup.CLASSIC)							MURDER(Murder.class),
	@Group(MechanicGroup.CLASSIC)							PAINTBALL(Paintball.class),
	@Group(MechanicGroup.CLASSIC)							QUAKE(Quake.class),
	@Group(MechanicGroup.CLASSIC)							SPLEEF(Spleef.class),
	@Group(MechanicGroup.CLASSIC) @Parent(Spleef.class)		SPLEGG(Splegg.class),
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

	public static final String BOUNDING_BOX_ID_PREFIX = "minigames_lobby_mechanic_";

	public static MechanicType from(CustomBoundingBoxEntity entity) {
		String id = entity.getId();
		if (Nullables.isNullOrEmpty(id))
			return null;

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

	@SneakyThrows
	public List<MinigameStatistic> getStatistics() {
		return ((MatchStatistics) Minigames.getMatchStatisticsMap().get(mechanic).newInstance((Match) null)).getStatistics();
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
