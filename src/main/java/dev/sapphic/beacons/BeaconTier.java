package dev.sapphic.beacons;

public enum BeaconTier {
  IRON, DIAMOND, NETHERITE;

  private static final BeaconTier[] TIERS = values();

  public static BeaconTier valueOf(final int ordinal) {
    return TIERS[ordinal & 2];
  }

  static BeaconTier of(final boolean diamond, final boolean netherite) {
    if (diamond != netherite) {
      return netherite ? NETHERITE : DIAMOND;
    }
    if (!diamond) {
      return IRON;
    }
    throw new IllegalStateException();
  }
}
