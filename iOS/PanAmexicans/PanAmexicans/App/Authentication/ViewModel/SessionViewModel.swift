//
//  SessionViewModel.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import Foundation

final class SessionViewModel: ObservableObject {

    // MARK: - Properties
    @Published var userData: UserData?

    private let authenticationRepository: AuthenticationRepository

    // MARK: - Initializers
    init(authenticationRepository: AuthenticationRepository = .init()) {
        self.authenticationRepository = authenticationRepository
    }

    @MainActor
    func setUserIfNeeded() async {
        guard userData == nil else { return }
        userData = try? await authenticationRepository.getCurrentUserData()
    }

    @MainActor
    func createAccount(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) async throws {
        self.userData = try await authenticationRepository.createAccount(
            name: name,
            lastName: lastName,
            email: email,
            password: password
        )
    }

    @MainActor
    func signIn(email: String, password: String) async throws {
        self.userData = try await authenticationRepository.signIn(
            email: email,
            password: password
        )
    }

    @MainActor
    func signOut() {
        authenticationRepository.signOut()
        userData = nil
    }
}
