package dev.sapphic.beacons;

public enum BeaconTier {
  IRON, DIAMOND, NETHERITE;

  private static final BeaconTier[] TIERS = values();

  public static BeaconTier valueOf(final int ordinal) {
    return TIERS[ordinal % TIERS.length];
  }

  public static BeaconTier get(final Object o) {
    return ((TieredBeacon) o).getTier();
  }

  public static void set(final Object o, final BeaconTier tier) {
    ((MutableTieredBeacon) o).setTier(tier);
  }

  static BeaconTier of(final boolean diamond, final boolean netherite) {
    if (diamond != netherite) {
      return netherite ? NETHERITE : DIAMOND;
    }

    if (!diamond) {
      return IRON;
    }

    throw new IllegalArgumentException();
  }
}
