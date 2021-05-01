package me.pugabyte.nexus.features.kits;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("kits")
@NoArgsConstructor
@Redirect(from = "/starter", to = "/kit starter")
public class KitCommand extends CustomCommand implements Listener {

	public KitCommand(CommandEvent event) {
		super(event);
	}

	static {
		new KitManager();
	}

	@Path()
	void list() {
		List<Kit> kits = Arrays.stream(KitManager.getAllKits())
			.filter(kit -> player().hasPermission("kit." + kit.getName().replace(" ", "_")))
			.collect(Collectors.toList());

		if (kits.size() == 0)
			error("There are no available kits");

		send("&3Available Kits:");
		line();
		for (Kit kit : kits)
			send(new JsonBuilder("&e" + StringUtils.camelCase(kit.getName())).hover("Click to receive the kit").command("kit " + kit.getName()));
	}

	@Path("reload")
	@Permission("group.staff")
	void reload() {
		KitManager.reloadConfig();
	}

	@Path("edit")
	@Permission("group.staff")
	void edit() {
		KitManagerProvider.getInv(null).open(player());
	}

	@Path("<kit>")
	void kit(Kit kit) {
		if (!player().hasPermission("kit." + kit.getName()))
			error("You do not have permission to use this kit");
		CooldownService service = new CooldownService();
		if (!service.check(player(), "kit-" + kit.getName(), kit.getDelay()))
			error("You must wait " + service.getDiff(player(), "kit-" + kit.getName()) + " before you can receive that kit again");
		PlayerUtils.giveItems(player(), Arrays.asList(kit.getItems()));
		send(PREFIX + "You have been given the &e" + StringUtils.camelCase(kit.getName()) + " &3kit");
	}

	@TabCompleterFor(Kit.class)
	List<String> tabCompleteKit(String filter) {
		List<String> list = new ArrayList<>();
		Kit[] kits = KitManager.getAllKits();
		for (Kit kit : kits)
			if (player().hasPermission("kit." + kit.getName().replace(" ", "_")))
				list.add(kit.getName().replace(" ", "_"));
		return list.stream().filter(name -> name.contains(filter)).collect(Collectors.toList());
	}

	@ConverterFor(Kit.class)
	Kit convertToKit(String value) {
		return KitManager.getByName(value);
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPlayedBefore()) return;
		if (KitManager.getAllKits().length == 0) return;
		PlayerUtils.giveItems(event.getPlayer(), Arrays.asList(KitManager.getByName("starter").getItems()));
	}

}
