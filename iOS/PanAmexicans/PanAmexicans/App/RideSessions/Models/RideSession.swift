//
//  RideSession.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import Foundation
import class FirebaseCore.Timestamp

struct RideSession: Codable {
    var id: String?
    var creator: String
    var rideSessionName: String
    var createdAt: Timestamp = Timestamp()
}

enum RideSessionStatus: String {
    case RIDING, BATHROOM, LAUNCH, DANGER
}
