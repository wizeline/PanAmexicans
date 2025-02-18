//
//  AuthenticationRepository.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import FirebaseAuth
import FirebaseFirestore

enum AuthenticationError: Error {
    case noUser
}

final class AuthenticationRepository {
    private let auth = Auth.auth()
    private let db = Firestore.firestore()

    private var usersCollection: CollectionReference {
        db.collection(Collection.USERS.rawValue)
    }

    func createAccount(
        name: String,
        lastName: String,
        email: String,
        password: String
    ) async throws -> UserData {
        let authResult = try await auth.createUser(withEmail: email, password: password)
        let userId = authResult.user.uid
        let userData = UserData(
            id: userId,
            email: email,
            firstName: name,
            lastName: lastName
        )

        try await addUser(userId, userData: userData)

        return userData
    }

    func signIn(email: String, password: String) async throws -> UserData {
        let authResult = try await auth.signIn(withEmail: email, password: password)
        return try await getUserData(for: authResult.user.uid)
    }

    func signOut() {
        do {
            try auth.signOut()
        } catch {
            print(error.localizedDescription)
        }
    }

    func getCurrentUserData() async throws -> UserData {
        guard let userId = auth.currentUser?.uid else {
            throw AuthenticationError.noUser
        }

        return try await getUserData(for: userId)
    }

    private func addUser(_ userId: String, userData: UserData) async throws {
        try usersCollection
            .document(userId)
            .setData(from: userData)
    }

    private func getUserData(for userId: String) async throws -> UserData {
        try await usersCollection
            .document(userId)
            .getDocument(as: UserData.self)
    }
}
