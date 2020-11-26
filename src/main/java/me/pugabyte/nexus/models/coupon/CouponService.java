package me.pugabyte.nexus.models.coupon;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.coupon.Coupons.Coupon;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Coupons.class)
public class CouponService extends MongoService {
	private final static Map<UUID, Coupons> cache = new HashMap<>();

	public Map<UUID, Coupons> getCache() {
		return cache;
	}

	@Override
	public <T> void save(T object) {
		Coupons coupons = (Coupons) object;
		if (coupons.getCoupons().isEmpty())
			super.delete(coupons);
		else {
			coupons.getCoupons().sort(Comparator.comparing(Coupon::getId));
			super.save(object);
		}
	}

}
