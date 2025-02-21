//
//  ActiveSessionView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct ActiveSessionView: View {
    @EnvironmentObject var rideSessionViewModel: RideSessionViewModel
    @EnvironmentObject var locationManager: LocationManager
    @State private var isLoading: Bool = false

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false

    let session: RideSession

    var body: some View {
        VStack {
            Text(session.rideSessionName)
                .font(.headline)

            Button {
                onLeave()
            } label: {
                Text("Leave")
            }

            ForEach(rideSessionViewModel.rideSessionUsers, id: \.id) { user in
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
        .redacted(reason: isLoading ? .placeholder : [])
        .onReceive(locationManager.$lastKnownLocation) { _ in
            updateSession()
        }
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(errorMessage)
        })
    }

    // MARK: - Helpers
    private func updateSession() {
        Task {
            guard let location = locationManager.lastKnownLocation else { return }

            await rideSessionViewModel.update(
                latitude: location.latitude,
                longitude: location.longitude
            )
        }
    }

    private func onLeave() {
        Task { @MainActor in
            do {
                isLoading = true
                try await rideSessionViewModel.leaveRideSession()
                isLoading = false
            } catch {
                isLoading = false
                errorMessage = error.localizedDescription
                showAlert = true
            }
        }
    }
}
