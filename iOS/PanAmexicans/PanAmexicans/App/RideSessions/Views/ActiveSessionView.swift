//
//  ActiveSessionView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct ActiveSessionView: View {
    @EnvironmentObject var rideSessionManager: RideSessionViewModel
    @EnvironmentObject var locationManager: LocationManager
    let session: RideSession

    var body: some View {
        VStack {
            Text(session.rideSessionName)
                .font(.headline)

            Button {
                Task {
                    await rideSessionManager.leaveRideSession()
                }
            } label: {
                Text("Leave")
            }

            ForEach(rideSessionManager.rideSessionUsers, id: \.id) { user in
                VStack {
                    Text("User: \(user.firstName)")
                    Text("Latitude: \(user.lat)")
                    Text("Longitude: \(user.lon)")
                    Text("Status: \(user.status)")
                }
                .padding()
                .background(Color.secondary.opacity(0.2))
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .padding()
                .frame(maxWidth: .infinity)
            }
        }
        .onReceive(locationManager.$lastKnownLocation) { _ in
            updateSession()
        }
    }

    // MARK: - Helpers
    private func updateSession() {
        Task {
            guard let location = locationManager.lastKnownLocation else { return }

            await rideSessionManager.update(
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }
}
