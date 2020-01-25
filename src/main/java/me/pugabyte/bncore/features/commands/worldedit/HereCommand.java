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
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;

@DoubleSlash
public class HereCommand extends CustomCommand {

	public HereCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("[integer]")
	void here(@Arg("0") int number) {
		World world = new BukkitWorld(player().getWorld());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		Player worldEditPlayer = worldEditPlugin.wrapPlayer(player());
		Vector pos = new WorldEditUtils(player().getWorld()).toVector(player().getLocation());
		Region region = new CuboidRegion(pos, pos);
		FaweAPI.wrapPlayer(player()).setSelection(region);
		session.getRegionSelector(world).explainPrimarySelection(worldEditPlayer, session, pos);
		session.getRegionSelector(world).explainSecondarySelection(worldEditPlayer, session, pos);
		if (number != 0) runCommand("expandall " + number);
	}
}

