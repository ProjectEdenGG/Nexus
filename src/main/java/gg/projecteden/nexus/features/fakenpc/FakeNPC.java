package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.SkinProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FakeNPC {
	@EqualsAndHashCode.Include
	@NonNull UUID uuid;
	int id;
	Location location;
	String name;
	ServerPlayer entityPlayer;
	SkinProperties skinProperties;
	boolean visible;
	Hologram hologram;

	public FakeNPC(Location location, String name) {
		this.uuid = UUID.randomUUID();
		this.location = location;
		this.name = name;
		this.skinProperties = new SkinProperties();
		this.visible = true;
		this.entityPlayer = new ServerPlayer(NMSUtils.getServer(), NMSUtils.toNMS(location.getWorld()), new GameProfile(uuid, name), null);
		this.hologram = new Hologram(List.of(name));
		this.createHologram();

		if (!Nullables.isNullOrEmpty(this.hologram.getLines()))
			this.entityPlayer.setCustomNameVisible(false);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		this.hologram.visible = visible;
	}

	public void applySkin() {
		ServerPlayer _entityPlayer = this.getEntityPlayer();
		SkinProperties skinProperties = this.skinProperties;

		GameProfile profile = _entityPlayer.getGameProfile();
		Property skinProperty = new Property("textures", skinProperties.getTexture(), skinProperties.getSignature());

		profile.getProperties().removeAll("textures"); // ensure client does not crash due to duplicate properties.
		profile.getProperties().put("textures", skinProperty);
	}

	public void createHologram() {
		List<ArmorStand> armorStands = new ArrayList<>();
		for (int i = 0; i < this.hologram.lines.size(); i++)
			armorStands.add(NMSUtils.createHologram(entityPlayer.getLevel()));

		this.hologram.setArmorStandList(armorStands);
	}

	@Data
	@NoArgsConstructor
	static class Hologram {
		private List<ArmorStand> armorStandList = new ArrayList<>();
		private List<String> lines;
		private boolean visible;
		private boolean localVisibility;
		private int radius;

		public Hologram(List<String> lines) {
			this(lines, true, true, 0);
		}

		public Hologram(List<String> lines, boolean visible, boolean localVisibility, int radius) {
			this.lines = lines;
			this.visible = visible;
			this.localVisibility = localVisibility;
			this.radius = radius;
		}

		public void setLine(int index, String string) {
			this.lines.set(index, string);
		}

		public String getLine(int index) {
			return this.lines.get(index);
		}
	}
}
