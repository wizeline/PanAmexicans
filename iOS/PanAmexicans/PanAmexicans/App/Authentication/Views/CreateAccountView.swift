//
//  CreateAccountView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 17/02/25.
//

import SwiftUI

struct CreateAccountView: View {
    @EnvironmentObject var sessionViewModel: SessionViewModel
    @State private var name: String = ""
    @State private var lastName: String = ""
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isSecured: Bool = true
    @State private var isLoading: Bool = false

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false

    private var isValid: Bool {
        !name.isEmpty &&
        !lastName.isEmpty &&
        !email.isEmpty &&
        !password.isEmpty
    }

    var body: some View {
        VStack {
            createTextField("Name", text: $name, prompt: "Enter your name")

            createTextField("Last Name", text: $lastName, prompt: "Enter your last name")

            createTextField("Email", text: $email, prompt: "Enter your email")
                .keyboardType(.emailAddress)

            ZStack(alignment: .trailing) {
                Group {
                    if isSecured {
                        SecureField("Password", text: $password, prompt: Text("Password"))
                    } else {
                        TextField("Password", text: $password)
                    }
                }
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .padding(.trailing, 32)

                Button {
                    isSecured.toggle()
                } label: {
                    Image(systemName: isSecured ? "eye.slash" : "eye")
                        .accentColor(.gray)
                }
            }
            .padding(10)
            .overlay {
                RoundedRectangle(cornerRadius: 10)
                    .stroke(.gray, lineWidth: 1)
            }
            .padding(.horizontal)

            createButton("Create account") {
                onCreateAccount()
            }
            .opacity(isValid ? 1 : 0.5)
            .disabled(!isValid)
        }
        .padding()
        .redacted(reason: isLoading ? .placeholder : [])
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(errorMessage)
        })
    }

    // MARK: - Helpers
    private func onCreateAccount() {
        Task { @MainActor in
            do {
                isLoading = true
                try await sessionViewModel.createAccount(
                    name: name,
                    lastName: lastName,
                    email: email,
                    password: password
                )
                isLoading = false
            } catch {
                isLoading = false
                errorMessage = error.localizedDescription
                showAlert = true
            }
        }
    }
}
