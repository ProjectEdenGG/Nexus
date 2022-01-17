package gg.projecteden.nexus.features.fakenpc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.projecteden.nexus.Nexus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FakeNPC {
	@EqualsAndHashCode.Include
	@NonNull UUID uuid;
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
		this.hologram = new Hologram();

		if (!this.hologram.getLines().isEmpty())
			this.entityPlayer.setCustomNameVisible(false);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		this.hologram.visible = visible;
	}

	public void applySkin() {
		ServerPlayer entityPlayer = this.getEntityPlayer();
		SkinProperties skinProperties = this.skinProperties;

		GameProfile profile = entityPlayer.getGameProfile();
		Property skinProperty = new Property("textures", skinProperties.getTexture(), skinProperties.getSignature());

		profile.getProperties().removeAll("textures"); // ensure client does not crash due to duplicate properties.
		profile.getProperties().put("textures", skinProperty);

		// this method is defined in Parchment (which is having issues with its dev bundle atm)
		// so it will not get obfuscated or anything (i.e. this code should not error)
		try {
			//noinspection JavaReflectionMemberAccess
			ServerPlayer.class.getMethod("setProfile", GameProfile.class).invoke(entityPlayer, profile);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException err) {
			Nexus.warn("Could not set profile of FakeNPC " + name + " (" + uuid + ")");
			err.printStackTrace();
		}
		this.setEntityPlayer(entityPlayer);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class SkinProperties {
		private String uuid;
		private String texture;
		private String signature;
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
			this.lines = lines;
			this.visible = true;
			this.localVisibility = false;
			this.radius = 0;
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
