package me.pugabyte.bncore.features.trust;

import com.griefcraft.lwc.LWC;
import me.pugabyte.bncore.BNCore;

public class TrustFeature {

	public TrustFeature() {
		LWC.getInstance().getModuleLoader().registerModule(BNCore.getInstance(), new LWCTrustModule());
	}

}
