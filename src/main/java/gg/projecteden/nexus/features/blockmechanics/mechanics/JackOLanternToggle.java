package gg.projecteden.nexus.features.blockmechanics.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.blockmechanics.BlockMechanicUtils;
import gg.projecteden.nexus.features.listeners.events.SourcedBlockRedstoneEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

// When powering a carved pumpkin, change into jack-o-lantern, & vice-versa
public class JackOLanternToggle implements Listener {

	public JackOLanternToggle() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneChange(SourcedBlockRedstoneEvent event) {
		if (!BlockMechanicUtils.passesFilter(event, event.getBlock())) return;
		if (event.isMinor())
			return;

		if (event.getBlock().getType() != Material.CARVED_PUMPKIN && event.getBlock().getType() != Material.JACK_O_LANTERN)
			return;

		if (event.isOn() == (event.getBlock().getType() == Material.JACK_O_LANTERN))
			return;

		setPowered(event.getBlock(), event.isOn());
	}

	private static void setPowered(Block block, boolean on) {
		BlockFace data = ((Directional) block.getBlockData()).getFacing();
		block.setType(on ? Material.JACK_O_LANTERN : Material.CARVED_PUMPKIN);
		Directional directional = (Directional) block.getBlockData();
		directional.setFacing(data);
		block.setBlockData(directional);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPumpkinBreak(BlockBreakEvent event) {
		if (!BlockMechanicUtils.passesFilter(event, event.getBlock())) return;

		if (event.getBlock().getType() != Material.CARVED_PUMPKIN && event.getBlock().getType() != Material.JACK_O_LANTERN)
			return;

		if (event.getBlock().getType() == Material.JACK_O_LANTERN && (event.getBlock().isBlockIndirectlyPowered() || event.getBlock().isBlockPowered()))
			event.setCancelled(true);
	}
}
