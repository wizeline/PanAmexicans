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
            UserAnnotation { _ in
                getBikeIcon(color: .accentColor)
            }

            ForEach(rideSessionViewModel.rideSessionUsers, id: \.id) { user in
                if user.id != rideSessionViewModel.userId {
                    Annotation(user.firstName, coordinate: .init(latitude: user.lat, longitude: user.lon)) {
                        getBikeIcon(color: .black)
                    }
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

    private func getBikeIcon(color: Color) -> some View {
        Image("bike")
            .renderingMode(.template)
            .resizable()
            .frame(width: 25, height: 25)
            .foregroundStyle(color)
    }
}
