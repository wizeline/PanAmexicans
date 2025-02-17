//
//  IconChangerView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import SwiftUI

struct IconChangerView: View {
    var body: some View {
        VStack {
            Button {
                changeAppIcon()
            } label: {
                Text("Change Icon")
            }
            .buttonStyle(.borderedProminent)
            .padding()
            .ignoresSafeArea()
        }
    }

    private func changeAppIcon() {
        let iconName: String? = UIApplication.shared.alternateIconName == "purple" ? nil : "purple"

        UIApplication.shared.setAlternateIconName(iconName) { error in
            if let error {
                print("Error setting alternate icon \(error.localizedDescription)")
            }
        }
    }
}
