//
//  RideSessionRepository.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import FirebaseFirestore

final class RideSessionRepository: ObservableObject {
    @Published var rideSessionUsers: [UserStatus] = []

    private let db: Firestore = Firestore.firestore()
    private let userId: String
    private var listener: ListenerRegistration?

    init(userId: String) {
        self.userId = userId
    }

    deinit {
        clearListener()
    }

    private var rideSessionCollection: CollectionReference {
        db.collection(Collection.RIDE_SESSIONS.rawValue)
    }

    func createAndJoinRideSession(displayName: String, userStatus: UserStatus) async throws -> RideSession {
        let rideSession = RideSession(
            creator: userId,
            rideSessionName: displayName
        )

        /// Create session
        let newSession = try rideSessionCollection.addDocument(from: rideSession)

        /// Join current user
        try newSession.collection(Collection.USERS.rawValue)
            .document(userId)
            .setData(from: userStatus)

        /// Get session data
        var session = try await newSession.getDocument(as: RideSession.self)
        session.id = newSession.documentID
        return session
    }

    func joinSession(_ sessionId: String, userStatus: UserStatus) async throws {
        try rideSessionCollection
            .document(sessionId)
            .collection(Collection.USERS.rawValue)
            .document(userId)
            .setData(from: userStatus)
    }

    func updateRideSession(_ rideSessionId: String, update: UserStatusUpdate) async throws {
        let data = try update.getDictionary()

        try await rideSessionCollection.document(rideSessionId)
            .collection(Collection.USERS.rawValue)
            .document(userId)
            .updateData(data)
    }

    func getCurrentSession() async -> RideSession? {
        guard let rideSessions = try? await getRideSessions() else { return nil }

        var currentSession: RideSession?

        for session in rideSessions {
            guard let sessionId = session.id else { continue }

            let userDocument = try? await rideSessionCollection
                .document(sessionId)
                .collection(Collection.USERS.rawValue)
                .document(userId)
                .getDocument()

            if let userDocument, userDocument.exists {
                currentSession = session
                break
            }
        }

        return currentSession
    }

    func getRideSessions() async throws -> [RideSession] {
        let snapshot = try await rideSessionCollection.getDocuments()

        return snapshot.documents.compactMap { document in
            var rideSession = try? document.data(as: RideSession.self)
            rideSession?.id = document.documentID
            return rideSession
        }
    }

    func leaveRideSession(_ rideSessionId: String) async throws {
        clearListener()

        let sessionReference = rideSessionCollection
            .document(rideSessionId)

        try await sessionReference
            .collection(Collection.USERS.rawValue)
            .document(userId)
            .delete()

        /// Delete if the session is now empty
        let users = try await sessionReference
            .collection(Collection.USERS.rawValue)
            .getDocuments()

        if users.count == .zero {
            try await sessionReference.delete()
        }
    }

    func startRideSessionListener(for rideSessionId: String) {
        clearListener()

        listener = rideSessionCollection
            .document(rideSessionId)
            .collection(Collection.USERS.rawValue)
            .addSnapshotListener { [weak self] snapshot, error in
                if error != nil {
                    self?.clearListener()
                    return
                }

                if let snapshot {
                    self?.rideSessionUsers = snapshot.documents.compactMap { try? $0.data(as: UserStatus.self) }
                }
            }
    }

    private func clearListener() {
        listener?.remove()
        listener = nil
    }
}
