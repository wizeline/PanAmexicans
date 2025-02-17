//
//  HomeView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import SwiftUI

struct HomeView: View {
    @EnvironmentObject var sessionManager: SessionManager

    var body: some View {
        NavigationStack {
            TabView {
                Tab("Sessions", systemImage: "person") {
                    SessionsView()
                }

                Tab("Map", systemImage: "map") {
                    MapView()
                }

                Tab("Icon", systemImage: "person") {
                    IconChangerView()
                }
            }
            .environmentObject(sessionManager)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        sessionManager.signOut()
                    } label: {
                        Text("Log Out")
                    }
                }
            }
        }
    }
}
