package gg.projecteden.nexus.models.fakenpcs.npcs.types;

import com.mojang.authlib.GameProfile;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils;
import gg.projecteden.nexus.features.fakenpc.FakeNPCUtils.SkinProperties;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCType;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlayerNPC extends FakeNPC {
	protected SkinProperties skinProperties;
	protected boolean mirror; // TODO: Switch to MirrorTrait

	public PlayerNPC(Player owner, String name) {
		super(FakeNPCType.PLAYER, owner, owner.getLocation(), name);
	}

	@Override
	public void createEntity() {
		this.entity = NMSUtils.createServerPlayer(getUuid(), getLocation(), getName());
		setSkin(getOwningUser());

		super.createEntity();
	}

	public ServerPlayer getEntityPlayer() {
		return (ServerPlayer) getEntity();
	}

	@Override
	public void spawn() {
		super.spawn();
		applySkin();
	}

	public void applySkin() {
		GameProfile profile = getEntityPlayer().getGameProfile();
		SkinProperties skinProperties = this.skinProperties;

		Property skinProperty = new Property("textures", skinProperties.getTexture(), skinProperties.getSignature());

		profile.properties().removeAll("textures"); // ensure client does not crash due to duplicate properties.
		profile.properties().put("textures", skinProperty.toNMS());
	}


	public void setSkin(Nerd nerd) {
		if (nerd.isOnline())
			setSkin(nerd.getPlayer());
		else
			setSkin(nerd.getOfflinePlayer());
	}

	public void setSkin(Player player) {
		setSkin(SkinProperties.of(player));
	}

	public void setSkin(HasUniqueId player) {
		setSkinFromUUID(player.getUniqueId().toString());
	}

	public void setSkin(String name) {
		setSkinFromUUID(FakeNPCUtils.getUUID(name));
	}

	private void setSkinFromUUID(String uuid) {
		setSkin(SkinProperties.of(uuid));
	}

	private void setSkin(@Nullable SkinProperties skinProperties) {
		if (skinProperties == null) {
			Nexus.warn("An error occurred when setting fakeNPC skin");
			return;
		}

		this.skinProperties = skinProperties;
		applySkin();
		Tasks.wait(1, this::respawn);
	}

	public CompletableFuture<Boolean> setMineSkin(String url) {
		return FakeNPCUtils.setMineSkin(this, url, true);
	}
}
