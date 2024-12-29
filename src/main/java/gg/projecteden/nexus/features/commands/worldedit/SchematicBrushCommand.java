package gg.projecteden.nexus.features.commands.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@DoubleSlash
@NoArgsConstructor
@Permission(Group.STAFF)
public class SchematicBrushCommand extends CustomCommand implements Listener {
	private static final String brushName = "Schematic Brush";

	public SchematicBrushCommand(CommandEvent event) {
		super(event);
	}

	@Path("<schematic>")
	@Description("Create a schematic brush tool")
	public void schemBrush(String schematic) {
		ItemStack tool = ItemUtils.getToolRequired(player());

		WorldEditUtils worldedit = new WorldEditUtils(player());
		Clipboard clipboard = worldedit.getSchematic(schematic);
		if (clipboard == null)
			error("Schematic " + schematic + " does not exist");

		ItemBuilder brush = new ItemBuilder(tool).name(brushName).lore(schematic);
		tool.setItemMeta(brush.build().getItemMeta());
	}

	@EventHandler
	public void onUseSchemBrush(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!Rank.of(player).isStaff()) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool)) return;

		ItemMeta meta = tool.getItemMeta();
		if (!meta.getDisplayName().equals(brushName)) return;

		List<String> lore = meta.getLore();
		if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(lore)) return;

		String schematic = StringUtils.stripColor(meta.getLore().get(0));
		WorldEditUtils worldedit = new WorldEditUtils(player);
		Clipboard clipboard = worldedit.getSchematic(schematic);
		if (clipboard == null) return;

		// So the player can undo if they want
		runCommand(player, "/schem load " + schematic);
		Tasks.wait(1, () -> runCommand(player, "/paste -a"));
	}
}
