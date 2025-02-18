//
//  LocationManager.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 12/02/25.
//

import CoreLocation
import MapKit

final class LocationManager: NSObject, ObservableObject {
    @Published var lastKnownLocation: CLLocationCoordinate2D?
    private let manager = CLLocationManager()

    // MARK: - Initializers
    override init() {
        super.init()
        manager.delegate = self
        manager.activityType = .automotiveNavigation
        manager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        manager.allowsBackgroundLocationUpdates = true
        manager.requestWhenInUseAuthorization()
        manager.requestAlwaysAuthorization()
        manager.startUpdatingLocation()
    }
}

// MARK: - LocationManager+CLLocationManagerDelegate
extension LocationManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        lastKnownLocation = locations.last?.coordinate
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationAuthorization()
    }

    func checkLocationAuthorization() {
        switch manager.authorizationStatus {
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        case .restricted:
            print("Location restricted")
        case .denied:
            print("Location denied")
        case .authorizedAlways, .authorizedWhenInUse:
            lastKnownLocation = manager.location?.coordinate
        @unknown default:
            print("Location service disabled")
        }
    }
}
