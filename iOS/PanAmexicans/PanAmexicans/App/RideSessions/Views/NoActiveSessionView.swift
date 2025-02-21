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
    @State private var selectedRideSession: RideSession?
    @State private var isLoading: Bool = true

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false

    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            HStack {
                Text("Active ride sessions")
                    .font(.headline)

                Spacer()

                Image(systemName: "arrow.clockwise")
                    .onTapGesture {
                        onRefresh()
                    }
            }

            if rideSessionViewModel.rideSessions.isEmpty {
                VStack {
                    Text("There are no active session available, try to refresh.")
                        .multilineTextAlignment(.center)
                        .font(.headline)
                        .padding(20)
                        .background(Color.gray.opacity(0.2))
                        .clipShape(RoundedRectangle(cornerRadius: 8))

                    Button {
                        onAddSessionTapped()
                    } label: {
                        Text("Create a session")
                            .font(.headline)
                    }
                }
            } else {
                VStack(alignment: .leading, spacing: 10) {
                    ForEach(rideSessionViewModel.rideSessions, id: \.id) { session in
                        HStack {
                            Label {
                                Text(session.rideSessionName)
                                    .font(.headline)
                            } icon: {
                                Image("bike")
                                    .resizable()
                                    .renderingMode(.template)
                                    .frame(width: 20, height: 20)
                                    .foregroundStyle(Color.accentColor)
                            }

                            Spacer()
                        }
                        .padding(12)
                        .background(Color.gray.opacity(0.2))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                        .contentShape(Rectangle())
                        .onTapGesture {
                            selectedRideSession = session
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            Spacer()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .redacted(reason: isLoading ? .placeholder : [])
        .padding()
        .task {
            await getRideSessions()
        }
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(errorMessage)
        })
        .sheet(item: $selectedRideSession) { session in
            RideSessionDetailView(session: session)
                .presentationDetents([.height(300)])
        }
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

    private func onRefresh() {
        Task {
            await getRideSessions()
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

    private func showAlert(with message: String) {
        isLoading = false
        errorMessage = message
        showAlert = true
    }
}
