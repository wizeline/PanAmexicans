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

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false
    
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
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(errorMessage)
        })
    }

    // MARK: - Helpers
    @MainActor
    private func getRideSessions() async {
        do {
            isLoading = true
            try await rideSessionViewModel.getRideSessions()
            isLoading = false
        } catch {
            showAlert(with: error.localizedDescription)
        }
    }

    private func onAddSessionTapped() {
        guard let location = locationManager.lastKnownLocation else { return }

        Task { @MainActor in
            do {
                isLoading = true

                try await rideSessionViewModel.createAndJoinRideSession(
                    latitude: location.latitude,
                    longitude: location.longitude
                )

                isLoading = false
            } catch {
                showAlert(with: error.localizedDescription)
            }
        }
    }

    private func onSessionTapped(_ session: RideSession) {
        guard let location = locationManager.lastKnownLocation,
              rideSessionViewModel.currentSession == nil else { return }

        Task { @MainActor in
            do {
                isLoading = true

                try await rideSessionViewModel.joinSession(
                    session,
                    latitude: location.latitude,
                    longitude: location.longitude
                )

                isLoading = false
            } catch {
                showAlert(with: error.localizedDescription)
            }
        }
    }

    private func showAlert(with message: String) {
        isLoading = false
        errorMessage = message
        showAlert = true
    }
}
