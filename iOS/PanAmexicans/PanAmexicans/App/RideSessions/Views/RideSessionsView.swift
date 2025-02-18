//
//  SessionsView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct RideSessionsView: View {
    @EnvironmentObject var rideSessionViewModel: RideSessionViewModel
    @EnvironmentObject var locationManager: LocationManager

    var body: some View {
        VStack {
            Group {
                if let session = rideSessionViewModel.currentSession {
                    ActiveSessionView(session: session)
                } else {
                    NoActiveSessionView()
                }
            }
            .environmentObject(rideSessionViewModel)
            .environmentObject(locationManager)

            Spacer()
        }
    }
}
