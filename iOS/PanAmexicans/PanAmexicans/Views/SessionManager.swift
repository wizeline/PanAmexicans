//
//  SessionManager.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import Foundation

final class SessionManager: ObservableObject {
    @Published var userData: UserData?
    @Published var alertError: String?
    @Published var currentSession: RideSession?
    @Published var rideSessions: [RideSession] = []
    @Published var rideSessionUsers: [UserStatus] = []

    var isUserLoggedIn: Bool {
        userData != nil
    }

    private let authenticationRepository = AuthenticationRepository()
    private let rideSessionRepository = RideSessionRepository()

    init() {
        rideSessionRepository
            .$rideSessionUsers
              .assign(to: &$rideSessionUsers)
    }

    @MainActor
    func setUserIfNeeded() async {
        guard !isUserLoggedIn else { return }
        userData = try? await authenticationRepository.getCurrentUserData()

        if let currentSession = await rideSessionRepository.getCurrentSession(),
           let sessionId = currentSession.id {
            self.rideSessionRepository.startRideSessionListener(for: sessionId)
            self.currentSession = currentSession
        }
    }

    @MainActor
    func createAccount(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) async {
        do {
            let userData = try await authenticationRepository.createAccount(
                name: name,
                lastName: lastName,
                email: email,
                password: password
            )

            self.userData = userData
        } catch {
            alertError = error.localizedDescription
        }
    }

    @MainActor
    func signIn(email: String, password: String) async {
        do {
            let userData = try await authenticationRepository.signIn(email: email, password: password)
            self.userData = userData
        } catch {
            alertError = error.localizedDescription
        }
    }

    func signOut() {
        authenticationRepository.signOut()
    }

    @MainActor
    func createAndJoinRideSession(latitude: Double, longitude: Double) async {
        guard let userData else { return }

        do {
            let session = try await rideSessionRepository.createAndJoinRideSession(
                displayName: "\(userData.firstName)'s Ride Session",
                userStatus: UserStatus(
                    id: userData.id,
                    firstName: userData.firstName,
                    lastName: userData.lastName,
                    lat: latitude,
                    lon: longitude,
                    status: RideSessionStatus.RIDING.rawValue
                )
            )

            if let sessionId = session.id {
                rideSessionRepository.startRideSessionListener(for: sessionId)
            }

            currentSession = session
        } catch {
            print(error.localizedDescription)
        }
    }

    func update(latitude: Double, longitude: Double) async {
        guard let sessionId = currentSession?.id else { return }

        try? await rideSessionRepository.updateRideSession(
            sessionId,
            update: UserStatusUpdate(lat: latitude, lon: longitude)
        )
    }

    @MainActor
    func leaveRideSession() async {
        guard let sessionId = currentSession?.id else { return }

        do {
            try await rideSessionRepository.leaveRideSession(sessionId)
            currentSession = nil
        } catch {
            print(error.localizedDescription)
        }
    }

    @MainActor
    func joinSession(_ session: RideSession, latitude: Double, longitude: Double) async {
        guard let userData, let sessionId = session.id else { return }

        do {
            let userStatus = UserStatus(
                id: userData.id,
                firstName: userData.firstName,
                lastName: userData.lastName,
                lat: latitude,
                lon: longitude,
                status: RideSessionStatus.RIDING.rawValue
            )

            try await rideSessionRepository.joinSession(sessionId, userStatus: userStatus)
            rideSessionRepository.startRideSessionListener(for: sessionId)
            currentSession = session
        } catch {
            print(error.localizedDescription)
        }
    }

    @MainActor
    func getRideSessions() async {
        do {
            rideSessions = try await rideSessionRepository.getRideSessions()
        } catch {
            print(error.localizedDescription)
        }
    }
}
