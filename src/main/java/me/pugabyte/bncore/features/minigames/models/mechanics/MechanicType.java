package me.pugabyte.bncore.features.minigames.models.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.*;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MechanicType {
	//Mechanic Class & ItemStack in GUI
	CAPTURE_THE_FLAG(new CaptureTheFlag(), new ItemStack(Material.BANNER, 1, DyeColor.RED.getDyeData())),
	DEATH_SWAP(new DeathSwap(), new ItemStack(Material.ENDER_PEARL)),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch(), new ItemStack(Material.DIAMOND_SWORD)),
	FREE_FOR_ALL(new FreeForAll(), new ItemStack(Material.IRON_SWORD)),
	ONE_FLAG_CAPTURE_THE_FLAG(new OneFlagCaptureTheFlag(), new ItemStack(Material.BANNER, 1, DyeColor.BLUE.getDyeData())),
	ONE_IN_THE_QUIVER(new OneInTheQuiver(), new ItemStack(Material.BOW)),
	PAINTBALL(new Paintball(), new ItemStack(Material.SNOW_BALL)),
	TEAM_DEATHMATCH(new TeamDeathmatch(), new ItemStack(Material.LEATHER_HELMET)),
	THIMBLE(new Thimble(), new ItemStack(Material.CHAINMAIL_HELMET));

	private Mechanic mechanic;
	private ItemStack itemStack;

	MechanicType(Mechanic mechanic, ItemStack itemStack) {
		this.mechanic = mechanic;
		this.itemStack = itemStack;
	}

	public Mechanic get() {
		return mechanic;
	}
	public ItemStack getItemStack() { return itemStack;}

}
