package gg.projecteden.nexus.models.coupon;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.coupon.Coupons.Coupon;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

@PlayerClass(Coupons.class)
public class CouponService extends MongoService<Coupons> {
	private final static Map<UUID, Coupons> cache = new ConcurrentHashMap<>();

	public Map<UUID, Coupons> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Coupons object) {
		return isNullOrEmpty(object.getCoupons());
	}

	@Override
	protected void beforeSave(Coupons coupons) {
		coupons.getCoupons().sort(Comparator.comparing(Coupon::getId));
	}

}
