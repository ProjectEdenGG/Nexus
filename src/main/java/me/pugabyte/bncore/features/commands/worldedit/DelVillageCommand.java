package me.pugabyte.bncore.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

@DoubleSlash
public class DelVillageCommand extends CustomCommand {

	public DelVillageCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path
	void delVillage() {
		World world = new BukkitWorld(player().getWorld());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());
		Vector pos = worldEditUtils.toVector(player().getLocation());
		Region region = new CuboidRegion(pos, pos);
		region = worldEditUtils.expandAll(region, 75);
		session.getRegionSelector(world).learnChanges();
		worldEditUtils.fill(region, Material.AIR);
		region.expand(Direction.DOWN.toVector().multiply(500));
		region.contract(Direction.DOWN.toVector().multiply(500));
		session.getRegionSelector(region.getWorld()).learnChanges();
		worldEditUtils.fill(region, Material.BEDROCK);
		region.expand(Direction.UP.toVector().multiply(3));
		region.contract(Direction.UP.toVector().multiply(1));
		session.getRegionSelector(region.getWorld()).learnChanges();
		worldEditUtils.fill(region, Material.GRASS);
		send("&3Successfully removed the village");
	}
}
