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
        VStack(spacing: 10) {
            HStack {
                Text(session.rideSessionName)
                    .font(.headline)

                Spacer()

                Button {
                    onLeave()
                } label: {
                    Text("Leave")
                }
            }

            ForEach(rideSessionViewModel.rideSessionUsers, id: \.id) { user in
                HStack {
                    Label {
                        VStack(alignment: .leading) {
                            Text("\(user.firstName) \(user.lastName)")
                                .font(.headline)
                            Text("\(user.lat), \(user.lon)")
                                .font(.caption)
                            Text("Status: \(user.status)")
                                .font(.caption)
                        }
                    } icon: {
                        Image(systemName: "person.circle")
                            .foregroundStyle(Color.accentColor)
                    }

                    Spacer()
                }
                .padding(12)
                .background(Color.gray.opacity(0.2))
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .contentShape(Rectangle())
            }

            Spacer()
        }
        .padding()
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
