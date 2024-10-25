package gg.projecteden.nexus.features.listeners.events.fake;

import com.gmail.nossr50.events.fake.FakeEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class FakeBlockBreakEvent extends BlockBreakEvent implements FakeEvent {

	public FakeBlockBreakEvent(Block block, Player player) {
		super(block, player);
	}

}
