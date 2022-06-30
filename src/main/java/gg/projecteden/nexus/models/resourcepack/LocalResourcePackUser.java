package gg.projecteden.nexus.models.resourcepack;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.left;

@Data
@Entity(value = "local_resource_pack", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LocalResourcePackUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private String saturnVersion;
	private String titanVersion;
	private String lastKnownSaturnVersion;
	private String lastKnownTitanVersion;

	public void setSaturnVersion(String saturnVersion) {
		this.enabled = !isNullOrEmpty(saturnVersion);
		this.saturnVersion = saturnVersion;
		this.lastKnownSaturnVersion = saturnVersion;
	}

	public void setTitanVersion(String titanVersion) {
		this.titanVersion = titanVersion;
		this.lastKnownTitanVersion = titanVersion;
	}

	public void forgetVersions() {
		saturnVersion = null;
		titanVersion = null;
	}

	public boolean hasTitan() {
		return !isNullOrEmpty(titanVersion);
	}

	@NotNull
	public String getSaturnStatus() {
		if (!isNullOrEmpty(saturnVersion))
			return "&aManual &3(&7" + left(saturnVersion, 7) + "&3)";
		else if (isEnabled())
			return "&aManual";
		else {
			if (isOnline()) {
				Status status = getOnlinePlayer().getResourcePackStatus();
				if (status != null)
					return STATUS_COLORS.get(status) + StringUtils.camelCase(status);
			}
		}

		return "&7Unknown";
	}

	public String getSaturnCommitUrl() {
		if (isNullOrEmpty(saturnVersion))
			return "";

		return EdenSocialMediaSite.GITHUB.getUrl() + "/Saturn/commit/" + saturnVersion;
	}

	public String getTitanStatus() {
		return hasTitan() ? "&aInstalled &3(&7" + getTitanVersion() + "&3)" : "&cNot installed";
	}

	public String getTitanCommitUrl() {
		if (isNullOrEmpty(titanVersion))
			return "";

		return EdenSocialMediaSite.GITHUB.getUrl() + "/Titan/commit/" + titanVersion;
	}

	private static final Map<Status, ChatColor> STATUS_COLORS = Map.of(
		Status.SUCCESSFULLY_LOADED, ChatColor.GREEN,
		Status.DECLINED, ChatColor.DARK_RED,
		Status.FAILED_DOWNLOAD, ChatColor.RED,
		Status.ACCEPTED, ChatColor.GOLD
	);

}
