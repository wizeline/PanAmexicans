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
            UserAnnotation()

            ForEach(rideSessionViewModel.rideSessionUsers, id: \.id) { user in
                Annotation(user.firstName, coordinate: .init(latitude: user.lat, longitude: user.lon)) {
                    Circle()
                        .foregroundStyle(Color.purple)
                        .frame(width: 20, height: 20)
                }
            }
        }
        .mapStyle(.standard(elevation: .realistic, pointsOfInterest: .excludingAll))
        .mapControls {
            MapUserLocationButton()
                .buttonBorderShape(.circle)

            MapCompass()
                .mapControlVisibility(.visible)
        }
        .controlSize(.large)
    }
}
