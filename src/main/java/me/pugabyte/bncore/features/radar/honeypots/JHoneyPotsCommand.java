package me.pugabyte.bncore.features.radar.honeypots;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases("jhp")
@Permission("group.staff")
public class JHoneyPotsCommand extends CustomCommand {
	SettingService service = new SettingService();
	WorldGuardUtils WGUtils = new WorldGuardUtils(player().getWorld());
	WorldEditUtils WEUtils = new WorldEditUtils(player().getWorld());

	public JHoneyPotsCommand(CommandEvent event) {
		super(event);
	}

	@Path("check <player>")
	@Permission("group.seniorstaff")
	void check(@Arg("self") OfflinePlayer player) {
		Setting setting = service.get(player.getUniqueId().toString(), "hpTriggered");
		if (setting == null) {
			send(PREFIX + "&e" + player.getName() + " &3has triggered 0 Honey Pots");
			return;
		}
		send(PREFIX + "&e" + player.getName() + "&3 has triggered &e" + setting.getValue() + " &3Honey Pots");
	}

	@Path("set <player> <int>")
	@Permission("group.seniorstaff")
	void set(@Arg("self") OfflinePlayer player, @Arg("0") int value) {
		Setting setting = service.get(player.getUniqueId().toString(), "hpTriggered");
		setting.setValue(String.valueOf(value));
		service.save(setting);
		send(PREFIX + "Successfully set &e" + player.getName() + " &3Honey Pots Triggered value to &e" + value);
	}

	@Path("(repair|reset) <honeyPot>")
	@Permission("group.seniorstaff")
	void repair(String honeyPot) {
		ProtectedRegion region = WGUtils.getProtectedRegion("hpregen_" + honeyPot);
		if (region == null) error("That honey pot does not exist");
		HoneyPots.fixHP(region, player().getWorld());
		send(PREFIX + "Successfully repaired the honey pot: &e" + honeyPot);
	}

	@SneakyThrows
	@Path("create <honeypot> [schemSize]")
	void create(String honeyPot, @Arg("10") int expand) {
		honeyPot = honeyPot.toLowerCase();
		Region selection = WEUtils.getPlayerSelection(player());
		RegionManager manager = WGUtils.getManager();
		ProtectedRegion region = new ProtectedCuboidRegion("hp_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		region.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
		region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
		region.setPriority(1);
		manager.addRegion(region);
		WEUtils.expandAll(selection, expand);
		ProtectedRegion schemRegion = new ProtectedCuboidRegion("hpregen_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		manager.addRegion(schemRegion);
		WEUtils.save("hp/" + honeyPot, selection);
		manager.save();
		send(PREFIX + "Successfully created the honey pot: &e" + honeyPot);
	}

	@SneakyThrows
	@Path("(delete|remove) <honeypot>")
	void delete(String honeyPot) {
		RegionManager manager = WGUtils.getManager();
		manager.removeRegion("hp_" + honeyPot);
		manager.removeRegion("hpregen_" + honeyPot);
		manager.save();
		send(PREFIX + "Successfully removed the honey pot: &e" + honeyPot);
	}

	@Path("list")
	void list() {
		Set<ProtectedRegion> regions = WGUtils.getRegionsLike("hp_");
		if (regions.size() == 0) error("There are no Honey Pots in your world.");
		send(PREFIX + "Honey Pots in your world:");
		AtomicInteger i = new AtomicInteger(1);
		regions.forEach((region) -> {
			json("&3" + i.getAndIncrement() + ".&e" + region.getId()).hover("&3Click to Teleport").command("honeypots teleport " + HoneyPots.getHP(region));
		});
	}

	@Path("(teleport|tp) <honeypot>")
	void teleport(String honeyPot) {
		Region region = WGUtils.getRegion("hp_" + honeyPot);
		if (region == null) error("That is not a valid Honey Pot");
		player().teleport(WEUtils.toLocation(region.getCenter()));
		send(PREFIX + "You have been teleported to Honey Pot:&e " + honeyPot);
	}

	@Path("help")
	void help() {
		usage();
	}

	@Path()
	void usage() {
		send("&c/hp < create | schem | delete | list | check | tp > <honeypot>");
	}


}

