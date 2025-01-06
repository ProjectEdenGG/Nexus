package gg.projecteden.nexus.models.coupon;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.coupon.Coupons.Coupon;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Coupons.class)
public class CouponService extends MongoPlayerService<Coupons> {
	private final static Map<UUID, Coupons> cache = new ConcurrentHashMap<>();

	public Map<UUID, Coupons> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Coupons object) {
		return Nullables.isNullOrEmpty(object.getCoupons());
	}

	@Override
	protected void beforeSave(Coupons coupons) {
		coupons.getCoupons().sort(Comparator.comparing(Coupon::getId));
	}

}
