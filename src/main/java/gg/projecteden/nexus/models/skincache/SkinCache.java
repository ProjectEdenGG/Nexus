package gg.projecteden.nexus.models.skincache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.dbassett.skullcreator.SkullCreator;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.*;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Entity(value = "skin_cache", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class SkinCache implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String value;
	private String signature;
	private LocalDateTime timestamp;
	private LocalDateTime lastChanged;

	private transient BufferedImage image;
	private SkinModel model;

	public static SkinCache of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static SkinCache of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static SkinCache of(UUID uuid) {
		return new SkinCacheService().get(uuid);
	}

	public boolean isCached() {
		return !Nullables.isNullOrEmpty(value);
	}

	public ItemStack getHead() {
		if (!isCached())
			update();

		if (!isCached())
			return getHeadFromPlayer();

		return new ItemBuilder(SkullCreator.itemFromBase64(value)).name(getNickname() + "'s Head").build();
	}

	public SkullMeta getHeadMeta() {
		return (SkullMeta) getHead().getItemMeta();
	}

	public SkinModel getModel() {
		return SkinModel.of(retrieveImage());
	}

	@SneakyThrows
	public BufferedImage retrieveImage() {
		if (image != null)
			return image;

		final String url = getTextureUrl();
		if (Nullables.isNullOrEmpty(url))
			return null;

		image = ImageIO.read(new URL(url));
		return image;
	}

	public static final Pattern TEXTURE_URL_REGEX = Pattern.compile("http://textures\\.minecraft\\.net/texture/[a-f\\d]{62,64}");

	public String getTextureUrl() {
		return getTextureUrl(this.value);
	}

	public String getTextureUrl(String value) {
		if (value == null)
			return "Null value";

		final String decoded = new String(Base64.getDecoder().decode(value));
		final Matcher matcher = TEXTURE_URL_REGEX.matcher(decoded);
		if (matcher.find())
			return matcher.group();

		return "Could not find url for " + getNickname() + ": " + value;
	}

	public boolean update() {
		String previous = this.value;
		ProfileProperty property;
		try {
			property = getCurrentProperty();
		} catch (NexusException ex) {
			Nexus.warn("Error getting " + getNickname() + "'s current player profile: " + ex.getMessage());
			return false;
		}

		this.timestamp = LocalDateTime.now();
		this.value = property.getValue();
		this.signature = property.getSignature();
		this.image = null;
		new SkinCacheService().save(this);

		final boolean changed = !getTextureUrl().equals(getTextureUrl(previous));

		if (changed)
			this.lastChanged = LocalDateTime.now();

		return changed;
	}

	@Data
	private static class FakePlayerProfile {
		private String id;
		private String name;
		private List<ProfileProperty> properties;

		private UUID getUuid() {
			return UUID.fromString(UUIDUtils.uuidFormat(id));
		}

		PlayerProfile getPlayerProfile() {
			PlayerProfile profile = Bukkit.getServer().createProfile(getUuid(), name);
			profile.setProperties(properties);
			return profile;
		}

	}

	private static String URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

	@SneakyThrows
	private @NotNull ProfileProperty getCurrentProperty() {
		if (isOnline())
			return getTextureProperty(getOnlinePlayer().getPlayerProfile());

		FakePlayerProfile fakeProfile = HttpUtils.mapJson(FakePlayerProfile.class, URL, UUIDUtils.uuidUnformat(uuid.toString()));
		return getTextureProperty(fakeProfile.getPlayerProfile());
	}

	private @NotNull ProfileProperty getTextureProperty(PlayerProfile profile) {
		if (profile != null)
			for (ProfileProperty property : profile.getProperties())
				if ("textures".equals(property.getName()))
					return property;

		throw new NexusException("No texture property:\n" + StringUtils.toPrettyString(profile));
	}

	private ItemStack getHeadFromPlayer() {
		return new ItemBuilder(Material.PLAYER_HEAD).skullOwner(this).build();
	}

	public enum SkinModel {
		STEVE,
		ALEX,
		;

		public static SkinModel of(BufferedImage image) {
			try {
				if (isTransparent(image, 47, 52)) return ALEX;
			} catch (ArrayIndexOutOfBoundsException ignore) {}

			return STEVE;
		}

		private static boolean isTransparent(BufferedImage image, int x, int y) {
			return isTransparent(image.getRGB(x, y));
		}

		private static boolean isTransparent(int rgb) {
			return rgb >> 24 == 0x00;
		}

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
