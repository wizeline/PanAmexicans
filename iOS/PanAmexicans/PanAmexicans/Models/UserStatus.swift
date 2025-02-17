//
//  UserStatus.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import Foundation
import class FirebaseCore.Timestamp

struct UserStatus: Codable {
    var id: String
    var firstName: String
    var lastName: String
    var lat: Double
    var lon: Double
    var status: String
    var updatedAt: Timestamp = Timestamp()
}
