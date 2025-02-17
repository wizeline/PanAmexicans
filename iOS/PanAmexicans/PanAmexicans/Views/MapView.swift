//
//  MapView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//


import MapKit
import SwiftUI

struct MapView: View {
    @StateObject var manager = LocationManager()
    @State private var selection: MapFeature?
    @State private var mapCameraPosition: MapCameraPosition = .automatic
    @State private var route: MKRoute?

    var body: some View {
        Map(initialPosition: .userLocation(fallback: .automatic), selection: $selection) {
            if let route {
                MapPolyline(route)
                    .stroke(.blue, lineWidth: 5)
            }
        }
        .mapStyle(.standard(elevation: .realistic, pointsOfInterest: .all))
        .mapFeatureSelectionAccessory(.callout(.compact))
        .onChange(of: selection) { _, selection in
            guard let currentLocation = manager.lastKnownLocation, let selection else { return }
            print(selection)

            getDirections(from: currentLocation, to: selection.coordinate)
        }
    }
}

extension MapView {
    func getDirections(
        from origin: CLLocationCoordinate2D,
        to destination: CLLocationCoordinate2D
    ) {
        route = nil
        let request = MKDirections.Request()
        request.source = MKMapItem(placemark: .init(coordinate: origin))
        request.destination = MKMapItem(placemark: .init(coordinate: destination))

        Task { @MainActor in
            let directions = MKDirections(request: request)
            let response = try? await directions.calculate()

            withAnimation {
                route = response?.routes.first
            }
        }
    }
}
