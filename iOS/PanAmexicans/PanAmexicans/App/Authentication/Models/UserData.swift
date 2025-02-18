//
//  UserData.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import Foundation

struct UserData: Codable {
    var id: String
    var email: String
    var firstName: String
    var lastName: String
    var photoUrl: String?
}
