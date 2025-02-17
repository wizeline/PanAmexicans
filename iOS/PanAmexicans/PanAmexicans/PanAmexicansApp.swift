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
    @StateObject var sessionManager = SessionManager()
    @State private var user: PMUser?

    var body: some Scene {
        WindowGroup {
            Group {
                if sessionManager.isUserLoggedIn {
                    HomeView()
                } else {
                    LoginView()
                }
            }
            .environmentObject(sessionManager)
            .onAppear {
                sessionManager.setUserIfNeeded()
            }
        }
    }
}
