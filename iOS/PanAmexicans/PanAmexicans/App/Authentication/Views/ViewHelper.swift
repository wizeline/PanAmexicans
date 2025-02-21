//
//  ViewHelper.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

extension View {
    func createTextField(
        _ label: String,
        text: Binding<String>,
        prompt: String
    ) -> some View {
        TextField(label, text: text, prompt: Text(prompt))
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
            .padding(10)
            .overlay {
                RoundedRectangle(cornerRadius: 10)
                    .stroke(.gray, lineWidth: 1)
            }
            .padding(.horizontal)
    }

    func createButton(
        _ title: String,
        _ action: @escaping @MainActor () -> Void
    ) -> some View {
        Button {
            action()
        } label: {
            Text(title)
                .font(.headline)
                .bold()
                .foregroundColor(.white)
        }
        .frame(height: 48)
        .frame(maxWidth: .infinity)
        .background(
            LinearGradient(colors: [.accent.opacity(0.8), .accent], startPoint: .topLeading, endPoint: .bottomTrailing)
        )
        .cornerRadius(16)
        .padding(.horizontal)
    }
}
