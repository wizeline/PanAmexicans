//
//  MapView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//


import MapKit
import SwiftUI

struct MapView: View {
    @EnvironmentObject var rideSessionViewModel: RideSessionViewModel

    var body: some View {
        Map(initialPosition: .userLocation(fallback: .automatic)) {
            ForEach(rideSessionViewModel.rideSessionUsers, id: \.id) { user in
                Marker(
                    coordinate: CLLocationCoordinate2D(
                        latitude: user.lat,
                        longitude: user.lon
                    )
                ) {
                    Text(user.firstName)
                }
            }
        }
        .mapStyle(.standard(elevation: .realistic, pointsOfInterest: .excludingAll))
    }
}
