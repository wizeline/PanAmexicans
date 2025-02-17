//
//  NoActiveSessionView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct NoActiveSessionView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @EnvironmentObject var locationManager: LocationManager
    @State private var isLoading: Bool = true

    var body: some View {
        List {
            Section {
                ForEach(sessionManager.rideSessions, id: \.id) { session in
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
        await sessionManager.getRideSessions()
        isLoading = false
    }

    private func onAddSessionTapped() {
        guard let location = locationManager.lastKnownLocation else { return }

        Task {
            await sessionManager.createAndJoinRideSession(
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }

    private func onSessionTapped(_ session: RideSession) {
        guard let location = locationManager.lastKnownLocation,
              sessionManager.currentSession == nil else { return }

        Task {
            await sessionManager.joinSession(
                session,
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }
}
