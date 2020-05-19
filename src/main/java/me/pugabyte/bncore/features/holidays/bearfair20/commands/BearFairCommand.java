package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.Fairgrounds;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.Merchants;
import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class BearFairCommand extends _WarpCommand {

	public BearFairCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR;
	}

	@Override
	@Path("warps list [filter]")
	@Permission("group.moderator")
	public void list(@Arg(tabCompleter = Warp.class) String filter) {
		super.list(filter);
	}

	@Override
	@Path("warps set <name>")
	@Permission("group.moderator")
	public void set(@Arg(tabCompleter = Warp.class) String name) {
		super.set(name);
	}

	@Override
	@Path("warps (rm|remove|delete|del) <name>")
	@Permission("group.moderator")
	public void delete(Warp warp) {
		super.delete(warp);
	}

	@Override
	@Path("warps (teleport|tp) <name>")
	@Permission("group.moderator")
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Override
	@Path("warps <name>")
	@Permission("group.moderator")
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@Path("warps tp nearest")
	public void teleportNearest() {
		getNearestWarp(player().getLocation()).ifPresent(warp -> warp.teleport(player()));
	}

	@Override
	@Path("warps nearest")
	@Permission("group.moderator")
	public void nearest() {
		super.nearest();
	}

	@Path("smite")
	public void smite() {
		commandBlock();
		BlockCommandSender sender = (BlockCommandSender) event.getSender();
		Location loc = sender.getBlock().getLocation();
		World world = loc.getWorld();
		if (world != null)
			world.strikeLightningEffect(loc);

	}

	@Path("merrygoround")
	public void merryGoRound() {
		commandBlock();
		Fairgrounds.startMerryGoRound();
	}

	@Path("trade")
	public void trade() {
		Location loc = player().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.mainRg);
		if (player().getWorld().equals(BearFair20.world) && WGUtils.getRegionsAt(loc).contains(region))
			Merchants.openMerchant(player());
		else
			error("Must be at Bear Fair!");
	}

	@Path
	void bearfair() {
		send(PREFIX + "Coming soon!");
	}
}
