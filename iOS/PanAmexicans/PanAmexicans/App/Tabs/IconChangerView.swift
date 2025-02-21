//
//  IconChangerView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 16/02/25.
//

import SwiftUI

struct IconChangerView: View {
    @State private var isEnabled: Bool

    private var isPremium: Bool {
        UIApplication.shared.alternateIconName == "AppIconPremium"
    }

    init() {
        let isPremium = UIApplication.shared.alternateIconName == "AppIconPremium"
        _isEnabled = State(wrappedValue: isPremium)
    }

    var body: some View {
        VStack {
            Toggle(isOn: $isEnabled) {
                Text("Enable your premium features")
                    .font(.headline)
            }
            .tint(.accent)

            Spacer()
        }
        .padding()
        .onChange(of: isEnabled) { _, enabled in
            changeAppIcon()
        }
    }

    private func changeAppIcon() {
        let iconName: String? = isPremium ? nil : "AppIconPremium"

        UIApplication.shared.setAlternateIconName(iconName) { error in
            if let error {
                print("Error setting alternate icon \(error.localizedDescription)")
            }
        }
    }
}
