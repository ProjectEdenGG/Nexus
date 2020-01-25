package me.pugabyte.bncore.features.commands.worldedit;

import com.boydti.fawe.config.BBC;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

import java.util.Arrays;

@DoubleSlash
public class ContractVCommand extends CustomCommand {

	public ContractVCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("[integer]")
	void expandAll(@Arg("1") int number) {
		World world = new BukkitWorld(player().getWorld());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		Region region = session.getSelection(world);
		int oldSize = region.getArea();
		region.contract(Arrays.stream(Direction.values())
				.filter(Direction::isUpright)
				.map(Direction::toVector)
				.map(vector -> vector.multiply(number))
				.toArray(Vector[]::new));
		session.getRegionSelector(world).learnChanges();
		int newSize = region.getArea();
		Player worldEditPlayer = worldEditPlugin.wrapPlayer(player());
		session.getRegionSelector(world).explainRegionAdjust(worldEditPlayer, session);
		BBC.SELECTION_EXPAND.send(worldEditPlayer, (newSize - oldSize));
	}
}

