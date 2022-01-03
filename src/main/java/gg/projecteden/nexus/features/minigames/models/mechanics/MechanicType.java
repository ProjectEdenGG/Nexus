package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.nexus.features.minigames.mechanics.*;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum MechanicType {
	ANVIL_DROP(new AnvilDrop(), MechanicGroup.ARCADE),
	ARCHERY(new Archery(), MechanicGroup.ARCADE),
	BATTLESHIP(new Battleship(), MechanicGroup.ARCADE),
	BINGO(new Bingo(), MechanicGroup.SURVIVAL),
	CAPTURE_THE_FLAG(new CaptureTheFlag()),
	DEATH_SWAP(new DeathSwap(), MechanicGroup.SURVIVAL),
	DOGFIGHTING(new Dogfighting()),
	FALLING_BLOCKS(new FallingBlocks(), MechanicGroup.ARCADE),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch()),
	FREE_FOR_ALL(new FreeForAll()),
	GOLD_RUSH(new GoldRush(), MechanicGroup.SUMMER_DOWN_UNDER),
	GRAB_A_JUMBUCK(new GrabAJumbuck(), MechanicGroup.SUMMER_DOWN_UNDER),
	HIDE_AND_SEEK(new HideAndSeek()),
	HOLE_IN_THE_WALL(new HoleInTheWall(), MechanicGroup.ARCADE),
	HOLI_SPLEGG(new HoliSplegg(), MechanicGroup.ARCADE),
	INFECTION(new Infection()),
	INVERTO_INFERNO(new InvertoInferno(), MechanicGroup.SUMMER_DOWN_UNDER),
	JUGGERNAUT(new Juggernaut()),
	KANGAROO_JUMPING(new KangarooJumping(), MechanicGroup.SUMMER_DOWN_UNDER),
	MASTERMIND(new Mastermind(), MechanicGroup.ARCADE),
	MAZE(new Maze()),
	MONSTER_MAZE(new MonsterMaze(), MechanicGroup.ARCADE),
	MURDER(new Murder()),
	ONE_FLAG_CAPTURE_THE_FLAG(new OneFlagCaptureTheFlag()),
	ONE_IN_THE_QUIVER(new OneInTheQuiver()),
	PAINTBALL(new Paintball()),
	PARKOUR(new Parkour()),
	PIXEL_DROP(new PixelDrop(), MechanicGroup.ARCADE),
	PIXEL_PAINTERS(new PixelPainters(), MechanicGroup.ARCADE),
	QUAKE(new Quake()),
	SABOTAGE(new Sabotage()),
	SIEGE(new Siege()),
	SPLEEF(new Spleef()),
	SPLEGG(new Splegg()),
	TEAM_DEATHMATCH(new TeamDeathmatch()),
	THIMBLE(new Thimble(), MechanicGroup.ARCADE),
	TNT_RUN(new TNTRun()),
	UHC(new UHC(), MechanicGroup.SURVIVAL),
	UNCIVIL_ENGINEERS(new UncivilEngineers(), MechanicGroup.SURVIVAL),
	XRUN(new XRun()),
	;

	MechanicType(@NotNull Mechanic mechanic) {
		this(mechanic, MechanicGroup.MECHANIC);
	}

	private final @NotNull Mechanic mechanic;
	private final @NotNull MechanicGroup group;

	public @NotNull Mechanic get() {
		return mechanic;
	}

	public @NotNull MechanicGroup getGroup() {
		return group;
	}
}
