//
//  SessionsView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct SessionsView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @StateObject private var locationManager = LocationManager()

    var body: some View {
        VStack {
            Group {
                if let session = sessionManager.currentSession {
                    ActiveSessionView(session: session)
                } else {
                    NoActiveSessionView()
                }
            }
            .environmentObject(sessionManager)
            .environmentObject(locationManager)

            Spacer()
        }
    }
}
