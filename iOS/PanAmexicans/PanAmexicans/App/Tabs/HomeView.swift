//
//  HomeView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import SwiftUI

struct HomeView: View {
    @StateObject private var rideSessionViewModel: RideSessionViewModel
    @StateObject private var locationManager = LocationManager()
    @State private var isLoading: Bool = true

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
            Tab("Sessions", systemImage: "person") {
                RideSessionsView()
                    .environmentObject(rideSessionViewModel)
                    .environmentObject(locationManager)
            }

            Tab("Map", systemImage: "map") {
                MapView()
                    .environmentObject(rideSessionViewModel)
            }

            Tab("Icon", systemImage: "pencil") {
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
