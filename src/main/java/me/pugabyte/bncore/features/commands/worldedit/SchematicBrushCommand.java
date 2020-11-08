package me.pugabyte.bncore.features.commands.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@NoArgsConstructor
@Permission("group.staff")
public class SchematicBrushCommand extends CustomCommand implements Listener {
	private static final String brushName = "Schematic Brush";

	public SchematicBrushCommand(CommandEvent event) {
		super(event);
	}

	@Path("<schematic>")
	public void schemBrush(String schematic) {
		ItemStack tool = Utils.getToolRequired(player());

		WorldEditUtils WEUtils = new WorldEditUtils(player());
		Clipboard clipboard = WEUtils.getSchematic(schematic);
		if (clipboard == null)
			error("Schematic " + schematic + " does not exist");

		ItemBuilder brush = new ItemBuilder(tool).name(brushName).lore(schematic);
		tool.setItemMeta(brush.build().getItemMeta());
	}

	@EventHandler
	public void onUseSchemBrush(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("group.staff")) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		ItemStack tool = Utils.getTool(player);
		if (Utils.isNullOrAir(tool)) return;

		ItemMeta meta = tool.getItemMeta();
		if (!meta.getDisplayName().equals(brushName)) return;

		List<String> lore = meta.getLore();
		if (Utils.isNullOrEmpty(lore)) return;

		String schematic = StringUtils.stripColor(meta.getLore().get(0));
		WorldEditUtils WEUtils = new WorldEditUtils(player);
		Clipboard clipboard = WEUtils.getSchematic(schematic);
		if (clipboard == null) return;

		// So the player can undo if they want
		runCommand(player, "/schem load " + schematic);
		Tasks.wait(1, () -> runCommand(player, "/paste -a"));
	}
}
