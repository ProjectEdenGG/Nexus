package gg.projecteden.nexus.features.test;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Env;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.inventivetalent.boundingbox.BoundingBoxAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

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
		return getArmorStand("b9a371f3-0963-4d3a-bc3b-efb4a5e62ba2");
	}

	@NotNull
	private static ArmorStand outline() {
		return getArmorStand("0ced311f-99ba-4dc3-bf45-0397bcfa23c0");
	}

	@Path("hide")
	void hide() {
		sendFakeItem(new ItemStack(Material.AIR));
	}

	@Path("show")
	void show() {
		sendFakeItem(new ItemBuilder(Material.PAPER).customModelData(1299).build());
	}

	private void sendFakeItem(ItemStack item) {
		PacketUtils.sendFakeItem(outline(), player(), item, EquipmentSlot.HEAD);
	}

	@Path("box [--multiplier] [--down] [--particle]")
	void box(@Switch double multiplier, @Switch double down, @Switch Particle particle) {
		final BoundingBox box = new BoundingBox();
		box.expand(0, -16, 0, 1 / multiplier);
		box.expand(32, 32, 0, 1 / multiplier);
		box.shift(-16 / multiplier, 8 / multiplier, 0 / multiplier);
		box.shift(0, -down / multiplier, 0);
		box.shift(outline().getEyeLocation());
		box.shift(outline().getEyeLocation().getDirection().multiply(.5));
		BoundingBoxAPI.setBoundingBox(outline(), toInventive(box));
		BoundingBoxAPI.drawParticleOutline(toInventive(box), world(), particle).run();
		final String command = StringUtils.getTeleportCommand(box.getCenter().toLocation(world()));
		send(json(command).command(command));
	}

	@NotNull
	private org.inventivetalent.boundingbox.BoundingBox toInventive(BoundingBox box) {
		return new org.inventivetalent.boundingbox.BoundingBox(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
	}

}
