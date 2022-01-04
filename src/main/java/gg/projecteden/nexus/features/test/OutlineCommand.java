package gg.projecteden.nexus.features.test;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.inventivetalent.boundingbox.BoundingBoxAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@Environments(Env.TEST)
public class OutlineCommand extends CustomCommand {

	public OutlineCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NotNull
	private static ArmorStand getArmorStand(String uuid) {
		return (ArmorStand) Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(uuid)));
	}

	@NotNull
	private static ArmorStand main() {
		return getArmorStand("4ec6976a-db6b-44ee-8368-272c91df8318");
	}

	@NotNull
	private static ArmorStand outline() {
		return getArmorStand("84917ffe-e2da-4dde-adc8-2130568dd141");
	}

	static void hide() {
		sendFakeItem(new ItemStack(Material.AIR));
	}

	static void show() {
		sendFakeItem(new ItemBuilder(Material.PAPER).customModelData(1299).build());
	}

	private static void sendFakeItem(ItemStack item) {
		PacketUtils.sendFakeItem(outline(), Dev.GRIFFIN.getOnlinePlayer(), item, EquipmentSlot.HEAD);
	}

	@Path("setBoundingBox [--multiplier] [--down] [--forward] [--particle]")
	void setBoundingBox(
		@Switch @Arg("13") double multiplier,
		@Switch @Arg("13.25") double down,
		@Switch @Arg(".625") double forward,
		@Switch @Arg("villager_happy") Particle particle
	) {
		final BoundingBox box = new BoundingBox();
		box.expand(0, -16, 0, 1 / multiplier);
		box.expand(32, 32, 0, 1 / multiplier);
		box.shift(-16 / multiplier, 8 / multiplier, 0 / multiplier);
		box.shift(0, -down / multiplier, 0);
		box.shift(outline().getEyeLocation());
		box.shift(outline().getEyeLocation().getDirection().multiply(forward));
		BoundingBoxAPI.setBoundingBox(outline(), box);
		BoundingBoxAPI.drawParticleOutline(box, world(), particle).run();
		final String command = StringUtils.getTeleportCommand(box.getCenter().toLocation(world()));
		send(json(command).command(command));
	}

	static {
		Tasks.repeat(0, 2, () -> {
			for (Player player : OnlinePlayers.getAll()) {
				Predicate<Player> isLookingAtImage = _player -> {
					final Entity entity = _player.getTargetEntity(15);
					if (!(entity instanceof ArmorStand))
						return false;
					if (!entity.getUniqueId().equals(outline().getUniqueId()))
						return false;

					return true;
				};

				if (isLookingAtImage.test(player))
					show();
				else
					hide();
			}
		});
	}

}
