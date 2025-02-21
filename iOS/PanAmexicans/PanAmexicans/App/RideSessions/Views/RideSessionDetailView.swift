//
//  RideSessionDetailView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 20/02/25.
//

import SwiftUI

struct RideSessionDetailView: View {
    @EnvironmentObject var rideSessionViewModel: RideSessionViewModel
    @EnvironmentObject var locationManager: LocationManager
    @State private var sessionUsers: [UserStatus] = []
    @State private var isLoading: Bool = true

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false

    let session: RideSession

    var body: some View {
        VStack(spacing: 10) {
            Text(session.rideSessionName)
                .font(.headline)
            Text(session.createdAt.dateValue().formatted(date: .abbreviated, time: .shortened))
                .font(.caption)
                .padding(.bottom, 20)

            ForEach(sessionUsers, id: \.id) { user in
                HStack {
                    Label {
                        Text("\(user.firstName) \(user.lastName)")
                            .font(.headline)
                    } icon: {
                        Image(systemName: "person.circle")
                            .foregroundStyle(Color.accentColor)
                    }

                    Spacer()
                }
                .padding(12)
                .background(Color.black.opacity(0.3))
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .contentShape(Rectangle())
            }

            Spacer()

            Button {
                onJoin()
            } label: {
                Text("Join")
                    .font(.headline)
                    .padding(.vertical, 6)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
        }
        .padding()
        .frame(maxWidth: .infinity)
        .redacted(reason: isLoading ? .placeholder : [])
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(errorMessage)
        })
        .task {
            await getRideSessionUsers()
        }
    }

    // MARK: - Helpers
    @MainActor
    private func getRideSessionUsers() async {
        do {
            isLoading = true
            sessionUsers = try await rideSessionViewModel.getRideSessionUsers(session.id)
            isLoading = false
        } catch {
            showAlert(with: error.localizedDescription)
        }
    }

    private func onJoin() {
        guard let location = locationManager.lastKnownLocation else { return }

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
