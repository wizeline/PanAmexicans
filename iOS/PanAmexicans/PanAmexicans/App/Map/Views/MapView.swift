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
                VStack {
                    getBikeIcon(color: .accentColor)

                    Text("You")
                        .font(.footnote)
                }
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
        .overlay(alignment: .topLeading) {
            if let session = rideSessionViewModel.currentSession {
                Text(session.rideSessionName)
                    .font(.footnote)
                    .bold()
                    .padding(.horizontal, 6)
                    .padding(.vertical, 5)
                    .background(Color.accentColor)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                    .padding()
            }
        }
    }

    // MARK: - Helpers
    private func getBikeIcon(color: Color) -> some View {
        Image("bike")
            .renderingMode(.template)
            .resizable()
            .frame(width: 30, height: 30)
            .foregroundStyle(color)
    }
}
