//
//  SessionManager.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import Foundation

final class RideSessionViewModel: ObservableObject {
    @Published var currentSession: RideSession?
    @Published var rideSessions: [RideSession] = []
    @Published var rideSessionUsers: [UserStatus] = []

    var userId: String {
        userData.id
    }

    private let userData: UserData
    private let rideSessionRepository: RideSessionRepository

    init(
        userData: UserData,
        rideSessionRepository: RideSessionRepository
    ) {
        self.userData = userData
        self.rideSessionRepository = rideSessionRepository

        self.rideSessionRepository
            .$rideSessionUsers
              .assign(to: &$rideSessionUsers)
    }

    @MainActor
    func setSessionIfNeeded() async {
        if let currentSession = await rideSessionRepository.getCurrentSession(),
           let sessionId = currentSession.id {
            self.rideSessionRepository.startRideSessionListener(for: sessionId)
            self.currentSession = currentSession
        }
    }

    @MainActor
    func createAndJoinRideSession(latitude: Double, longitude: Double) async throws {
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
    }

    func update(latitude: Double, longitude: Double) async {
        guard let sessionId = currentSession?.id else { return }

        try? await rideSessionRepository.updateRideSession(
            sessionId,
            update: UserStatusUpdate(lat: latitude, lon: longitude)
        )
    }

    @MainActor
    func leaveRideSession() async throws {
        guard let sessionId = currentSession?.id else { return }

        try await rideSessionRepository.leaveRideSession(sessionId)
        currentSession = nil
        rideSessionUsers.removeAll()
    }

    @MainActor
    func joinSession(_ session: RideSession, latitude: Double, longitude: Double) async throws {
        guard let sessionId = session.id else { return }

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
    }

    @MainActor
    func getRideSessions() async throws {
        rideSessions = try await rideSessionRepository.getRideSessions()
    }
}
