//
//  PanAmexicansApp.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 10/02/25.
//

import FirebaseCore
import SwiftUI

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct PanAmexicansApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject private var sessionViewModel = SessionViewModel()
    @State private var isLoading: Bool = true

    var body: some Scene {
        WindowGroup {
            Group {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(.circular)
                } else {
                    if let userData = sessionViewModel.userData {
                        TabsView(userData: userData)
                            .environmentObject(sessionViewModel)
                    } else {
                        LoginView()
                            .environmentObject(sessionViewModel)
                    }
                }
            }
            .task { @MainActor in
                await sessionViewModel.setUserIfNeeded()
                isLoading = false
            }
        }
    }
}
