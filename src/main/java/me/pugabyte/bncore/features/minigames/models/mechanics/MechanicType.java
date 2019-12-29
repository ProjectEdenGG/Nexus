package me.pugabyte.bncore.features.minigames.models.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.CaptureTheFlag;
import me.pugabyte.bncore.features.minigames.mechanics.DeathSwap;
import me.pugabyte.bncore.features.minigames.mechanics.FourTeamDeathmatch;
import me.pugabyte.bncore.features.minigames.mechanics.FreeForAll;
import me.pugabyte.bncore.features.minigames.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.bncore.features.minigames.mechanics.OneInTheQuiver;
import me.pugabyte.bncore.features.minigames.mechanics.Paintball;
import me.pugabyte.bncore.features.minigames.mechanics.TeamDeathmatch;
import me.pugabyte.bncore.features.minigames.mechanics.Thimble;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MechanicType {
	//Mechanic Class & Material of ItemStack in GUI
	CAPTURE_THE_FLAG(new CaptureTheFlag(), Material.BANNER),
	DEATH_SWAP(new DeathSwap(), Material.ENDER_PEARL),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch(), Material.DIAMOND_SWORD),
	FREE_FOR_ALL(new FreeForAll(), Material.IRON_SWORD),
	ONE_FLAG_CAPTURE_THE_FLAG(new OneFlagCaptureTheFlag(), Material.BANNER),
	ONE_IN_THE_QUIVER(new OneInTheQuiver(), Material.BOW),
	PAINTBALL(new Paintball(), Material.SNOW_BALL),
	TEAM_DEATHMATCH(new TeamDeathmatch(), Material.LEATHER_HELMET),
	THIMBLE(new Thimble(), Material.CHAINMAIL_HELMET);

	private Mechanic mechanic;
	private Material material;

	MechanicType(Mechanic mechanic, Material material) {
		this.mechanic = mechanic;
		this.material = material;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}
	public Material getMaterial() { return material;}

}
