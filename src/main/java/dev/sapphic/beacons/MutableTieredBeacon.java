package dev.sapphic.beacons;

public interface MutableTieredBeacon extends TieredBeacon {
  void setTier(final BeaconTier tier);
}
