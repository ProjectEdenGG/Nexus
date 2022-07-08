package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.mechanics.*;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum MechanicType {
	ANVIL_DROP(AnvilDrop.class),
	ARCHERY(Archery.class),
	BATTLESHIP(Battleship.class),
	BINGO(Bingo.class),
	CAPTURE_THE_FLAG(CaptureTheFlag.class),
	DEATH_SWAP(DeathSwap.class),
	DOGFIGHTING(Dogfighting.class),
	FALLING_BLOCKS(FallingBlocks.class),
	FOUR_TEAM_DEATHMATCH(FourTeamDeathmatch.class),
	FREE_FOR_ALL(FreeForAll.class),
	GOLD_RUSH(GoldRush.class),
	GRAB_A_JUMBUCK(GrabAJumbuck.class),
	HIDE_AND_SEEK(HideAndSeek.class),
	HOLE_IN_THE_WALL(HoleInTheWall.class),
	HOLI_SPLEGG(HoliSplegg.class),
	INFECTION(Infection.class),
	INVERTO_INFERNO(InvertoInferno.class),
	JUGGERNAUT(Juggernaut.class),
	KANGAROO_JUMPING(KangarooJumping.class),
	MASTERMIND(Mastermind.class),
	MAZE(Maze.class),
	MONSTER_MAZE(MonsterMaze.class),
	MURDER(Murder.class),
	FLAG_RUSH(FlagRush.class),
	ONE_IN_THE_QUIVER(OneInTheQuiver.class),
	PAINTBALL(Paintball.class),
	PARKOUR(Parkour.class),
	PIXEL_DROP(PixelDrop.class),
	PIXEL_PAINTERS(PixelPainters.class),
	QUAKE(Quake.class),
	SABOTAGE(Sabotage.class),
	SIEGE(Siege.class),
	SPLEEF(Spleef.class),
	SPLEGG(Splegg.class),
	TEAM_DEATHMATCH(TeamDeathmatch.class),
	THIMBLE(Thimble.class),
	TNT_RUN(TNTRun.class),
	UHC(UHC.class),
	UNCIVIL_ENGINEERS(UncivilEngineers.class),
	XRUN(XRun.class),
	;

	MechanicType(Class<? extends Mechanic> mechanic) {
		this.mechanic = Nexus.singletonOf(mechanic);
	}

	private final @NotNull Mechanic mechanic;

	public @NotNull Mechanic get() {
		return mechanic;
	}

}
