//
//  SessionManager.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import FirebaseAuth

final class SessionManager: ObservableObject {
    @Published var user: PMUser?
    @Published var alertError: String?

    var isUserLoggedIn: Bool {
        user != nil
    }

    private let firebaseAuth = Auth.auth()
    private let db = Firestore.firestore()

    func setUserIfNeeded() {
        guard !isUserLoggedIn else { return }
        user = firebaseAuth.currentUser
    }
    
    func createAccount(email: String, password: String) {
        firebaseAuth.createUser(withEmail: email, password: password) { [weak self] authResult, error in
            guard let self else { return }

            guard let authResult else {
                alertError = error?.localizedDescription
                return
            }

            addUser(UserData(email: email, firstName: "Karen", lastName: "Dev"))
            self.user = authResult.user
        }
    }

    func signIn(email: String, password: String) {
        firebaseAuth.signIn(withEmail: email, password: password) { [weak self] authResult, error in
            guard let self else { return }

            guard let authResult else {
                alertError = error?.localizedDescription
                return
            }

            self.user = authResult.user
        }
    }

    func signOut() {
        do {
            try firebaseAuth.signOut()
            user = nil
        } catch {
            print(error.localizedDescription)
        }
    }

    func addUser(_ user: UserData) {
        let collection = db.collection(Collection.USERS.rawValue)

        do {
            let newUser = try collection.addDocument(from: user)
            print("User stored with new document reference: \(newUser)")
        }
        catch {
            print(error)
        }
    }
}

enum Collection: String {
    case RIDE_SESSIONS, USERS
}

import FirebaseFirestore

struct UserData: Codable {
    @DocumentID var id: String?
    var email: String?
    var firstName: String?
    var lastName: String?
    var photoUrl: String?
}
