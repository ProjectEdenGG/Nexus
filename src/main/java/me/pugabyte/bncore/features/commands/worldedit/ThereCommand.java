package me.pugabyte.bncore.features.commands.worldedit;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

@DoubleSlash
public class ThereCommand extends CustomCommand {

	public ThereCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("[integer]")
	void there(@Arg("0") int number) {
		World world = new BukkitWorld(player().getWorld());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		Player worldEditPlayer = worldEditPlugin.wrapPlayer(player());
		Vector pos = worldEditPlayer.getBlockTrace(300);
		Vector pos2 = worldEditPlayer.getBlockTrace(300);
		Region region = new CuboidRegion(pos, pos2);
		FaweAPI.wrapPlayer(player()).setSelection(region);
		session.getRegionSelector(world).explainPrimarySelection(worldEditPlayer, session, pos);
		session.getRegionSelector(world).explainSecondarySelection(worldEditPlayer, session, pos2);
		if (number != 0) runCommand("expandall " + number);
	}
}

