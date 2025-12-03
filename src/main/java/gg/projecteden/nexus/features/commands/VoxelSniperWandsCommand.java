package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import lombok.NonNull;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Aliases({"vsw", "vswands"})
public class VoxelSniperWandsCommand extends CustomCommand {

	public VoxelSniperWandsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state] [player]")
	@Description("Toggle VoxelSniper on or off")
	void toggle(Boolean state, @Arg(value = "self", permission = Group.STAFF) Nerd nerd) {
		if (state == null)
			state = !nerd.isVoxelSniperEnabled();

		nerd.setVoxelSniperEnabled(state);
		new NerdService().save(nerd);
		send(PREFIX + "VoxelSniper " + (state ? "&aenabled" : "&cdisabled"));
	}

	static {
		LuckPermsUtils.registerContext(new VoxelSniperCalculator());
	}

	public static class VoxelSniperCalculator implements ContextCalculator<Player> {

		@Override
		public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
			boolean enabled = Nerd.of(target).isVoxelSniperEnabled();

			if (Minigamer.of(target).isPlaying())
				enabled = false;

			contextConsumer.accept("voxelsniper", enabled ? "true" : "false");
		}

		@Override
		public ContextSet estimatePotentialContexts() {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			builder.add("voxelsniper", "true");
			builder.add("voxelsniper", "false");
			return builder.build();
		}

	}

}

