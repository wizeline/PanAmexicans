//
//  NoActiveSessionView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct NoActiveSessionView: View {
    @EnvironmentObject var rideSessionViewModel: RideSessionViewModel
    @EnvironmentObject var locationManager: LocationManager
    @State private var isLoading: Bool = true

    var body: some View {
        List {
            Section {
                ForEach(rideSessionViewModel.rideSessions, id: \.id) { session in
                    Text(session.rideSessionName)
                        .onTapGesture {
                            onSessionTapped(session)
                        }
                }
            } header: {
                HStack {
                    Text("Join a ride session:")
                        .font(.headline)

                    Spacer()

                    Button {
                        onAddSessionTapped()
                    } label: {
                        Image(systemName: "plus.circle.fill")
                            .foregroundStyle(Color.black)
                    }
                }
            }
        }
        .redacted(reason: isLoading ? .placeholder : [])
        .task {
            await getRideSessions()
        }
    }

    // MARK: - Helpers
    @MainActor
    private func getRideSessions() async {
        isLoading = true
        await rideSessionViewModel.getRideSessions()
        isLoading = false
    }

    private func onAddSessionTapped() {
        guard let location = locationManager.lastKnownLocation else { return }

        Task {
            await rideSessionViewModel.createAndJoinRideSession(
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }

    private func onSessionTapped(_ session: RideSession) {
        guard let location = locationManager.lastKnownLocation,
              rideSessionViewModel.currentSession == nil else { return }

        Task {
            await rideSessionViewModel.joinSession(
                session,
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }
}
