package dev.trotrohailer.shared.util.location;

import android.location.Location;

public interface MyLocationProvider {
    boolean startLocationProvider(MyLocationConsumer myLocationConsumer);

    void stopLocationProvider();

    Location getLastKnownLocation();

    void destroy();
}
