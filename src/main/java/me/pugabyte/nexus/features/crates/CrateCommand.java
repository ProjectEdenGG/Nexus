package me.pugabyte.nexus.features.crates;

import me.pugabyte.nexus.features.crates.menus.CrateEditMenu;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.Name;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;

import java.util.Arrays;

import static me.pugabyte.nexus.utils.SoundUtils.getPitch;

@Aliases("crates")
public class CrateCommand extends CustomCommand {

	public CrateCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void info() {
		line();
		send("&3Hi there, I'm &eBlast.");
		line();
		send("&3These here are our server's &eCrates&3. They can give you amazing rewards to help boost your survival experience.");
		send("&3To open a Crate, you must have a &eCrate Key&3. You can get these from &evoting&3, &eevents&3, &eand more&3!");
		line();
		send("&3To &epreview rewards&3, you can &eright-click with an empty hand &3to open a preview menu.");
		send("&3To &eopen multiple at a time&3, simply &eshift-click &3with multiple keys in your hand.");
		line();
		send("&3I hope you enjoy, and have a good day!");
	}

	@Path("give <type> [player] [amount]")
	@Permission("group.admin")
	void key(CrateType type, @Arg("self") OfflinePlayer player, @Arg("1") Integer amount) {
		type.give(player, amount);
		if (player.isOnline())
			send(player.getPlayer(), Crates.PREFIX + "You have been given &e" + amount + " " + StringUtils.camelCase(type.name()) +
					" Crate Key" + (amount == 1 ? "" : "s"));
		if (!isSelf(player))
			send(Crates.PREFIX + "You gave &e" + amount + " " + StringUtils.camelCase(type.name()) + " Crate Key" +
					(amount == 1 ? "" : "s") + "  &3to &e" + Name.of(player));
	}

	@Path("animationAndSoundTest")
	@Permission("group.staff")
	void animation() {
		Location location = LocationUtils.getCenteredLocation(new Location(Bukkit.getWorld("buildadmin"), -434.00, 4.00, 2410.00, .00F, .00F));
		CrateType.VOTE.getCrateClass().playAnimation(location).thenAccept(location1 ->
				CrateType.VOTE.getCrateClass().playFinalParticle(location1));
		int wait = 3;
		float volume = .6F;
		World w = location.getWorld();
		Tasks.wait(wait += 0, () -> {
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, .6F, getPitch(3));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(3));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
		});
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(3)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(5)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(6)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
		Tasks.wait(wait += 3, () -> {
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(5));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(12));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
		});
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(5)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(8)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
		Tasks.wait(wait += 3, () -> {
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(7));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(10));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(14));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
		});
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(7)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(10)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(11)));
		Tasks.wait(wait += 3, () -> {
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(9));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(13));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(16));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
		});
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(9)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(11)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(12)));
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, volume, getPitch(13)));
		Tasks.wait(wait += 3, () -> {
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(13));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(17));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(8));
			w.playSound(location, Sound.BLOCK_NOTE_BLOCK_SNARE, .5F, getPitch(24));
		});
		Tasks.wait(wait += 3, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(18)));
		Tasks.wait(wait += 2, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(20)));
		Tasks.wait(wait += 2, () -> w.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, .6F, getPitch(25)));
	}

	@Path("edit [filter]")
	@Permission("group.admin")
	void edit(@Arg("ALL") CrateType filter) {
		CrateEditMenu.getMenu(filter, null).open(player());
	}

	@Path("reset [crate]")
	@Permission("group.admin")
	@Description("Resets a crate (or all crates if no crate is specified) if it is stuck or errors")
	void reset(CrateType type) {
		if (type == null)
			Arrays.stream(CrateType.values()).filter(crateType -> crateType != CrateType.ALL).forEach(crateType -> type.getCrateClass().reset());
		else
			type.getCrateClass().reset();
	}

}
