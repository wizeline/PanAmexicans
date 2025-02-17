//
//  UserStatusUpdate.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import Foundation
import class FirebaseCore.Timestamp
import class FirebaseFirestore.Firestore

struct UserStatusUpdate: Encodable {
    var lat: Double
    var lon: Double
    var updatedAt: Timestamp = Timestamp()

    var dictionary: [String: Any]? {
        try? Firestore.Encoder().encode(self)
    }
}
