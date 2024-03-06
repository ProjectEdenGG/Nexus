package gg.projecteden.nexus.features.kits;

import gg.projecteden.nexus.features.listeners.events.FirstWorldGroupVisitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

	@Path
	@Description("List available kits")
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
	@Permission(Group.STAFF)
	@Description("Reload kits from disk")
	void reload() {
		KitManager.reloadConfig();
	}

	@Path("edit")
	@Permission(Group.STAFF)
	@Description("Open the kit editor menu")
	void edit() {
		new KitManagerProvider().open(player());
	}

	@Path("<kit>")
	@Description("Spawn a kit")
	void kit(Kit kit) {
		WorldGroup worldGroup = worldGroup();
		if (worldGroup != WorldGroup.SURVIVAL && worldGroup != WorldGroup.CREATIVE)
			error("Kits may only be claimed in survival and creative");
		if (!player().hasPermission("kit." + kit.getName()))
			error("You do not have permission to use this kit");
		CooldownService service = new CooldownService();
		String cooldownKey = "kit-" + kit.getName() + "-" + worldGroup.name();
		if (!service.check(player(), cooldownKey, kit.getDelay()))
			error("You must wait " + service.getDiff(player(), cooldownKey) + " before you can receive that kit again");
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
		return list.stream().filter(name -> name.toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
	}

	@ConverterFor(Kit.class)
	Kit convertToKit(String value) {
		return KitManager.getByName(value);
	}

	@EventHandler
	public void onFirstJoin(FirstWorldGroupVisitEvent event) {
		if (event.getWorldGroup() != WorldGroup.SURVIVAL)
			return;

		if (!event.getPlayer().getInventory().isEmpty())
			return;

		if (KitManager.getAllKits().length == 0)
			return;

		PlayerUtils.giveItems(event.getPlayer(), Arrays.asList(KitManager.getByName("starter").getItems()));
	}

}
