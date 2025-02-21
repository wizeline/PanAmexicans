//
//  HomeView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import SwiftUI

struct TabsView: View {
    @EnvironmentObject private var sessionViewModel: SessionViewModel
    @StateObject private var rideSessionViewModel: RideSessionViewModel
    @StateObject private var locationManager = LocationManager()
    @State private var isLoading: Bool = true

    private var welcomeMessage: String {
        "Welcome, \(sessionViewModel.userName ?? "")!"
    }

    init(userData: UserData) {
        _rideSessionViewModel = StateObject(
            wrappedValue: RideSessionViewModel(
                userData: userData,
                rideSessionRepository: RideSessionRepository(userId: userData.id)
            )
        )
    }

    var body: some View {
        TabView {
            Tab("Sessions", systemImage: "person.3.fill") {
                NavigationStack {
                    RideSessionsView()
                        .environmentObject(rideSessionViewModel)
                        .environmentObject(locationManager)
                        .navigationTitle(welcomeMessage)
                        .toolbarVisibility(.visible, for: .navigationBar)
                        .toolbar {
                            ToolbarItem(placement: .topBarTrailing) {
                                Button {
                                    sessionViewModel.signOut()
                                } label: {
                                    Text("Log Out")
                                }
                            }
                        }
                }
            }

            Tab("Map", systemImage: "map") {
                MapView()
                    .environmentObject(rideSessionViewModel)
            }

            Tab("Premium", systemImage: "medal.fill") {
                IconChangerView()
            }
        }
        .redacted(reason: isLoading ? .placeholder : [])
        .task { @MainActor in
            await rideSessionViewModel.setSessionIfNeeded()
            isLoading = false
        }
    }
}
