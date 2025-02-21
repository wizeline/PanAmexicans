//
//  ContentView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 10/02/25.
//

import SwiftUI

struct LoginView: View {
    @EnvironmentObject var sessionViewModel: SessionViewModel
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isSecured: Bool = true
    @State private var isLoading: Bool = false

    // MARK: - Error alert
    @State private var errorMessage: String = ""
    @State private var showAlert: Bool = false

    private var isValid: Bool {
        !email.isEmpty && !password.isEmpty
    }

    var body: some View {
        NavigationStack {
            VStack {
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

                createButton("Sign In") {
                    onSignIn()
                }
                .opacity(isValid ? 1 : 0.5)
                .disabled(!isValid)

                NavigationLink {
                    CreateAccountView()
                        .environmentObject(sessionViewModel)
                } label: {
                    Text("Create account")
                        .font(.subheadline)
                        .foregroundStyle(Color.accent)
                }
            }
            .padding()
            .redacted(reason: isLoading ? .placeholder : [])
            .alert("", isPresented: $showAlert, actions: {
                Button { } label: { Text("Ok") }
            }, message: {
                Text(errorMessage)
            })
        }
    }

    // MARK: - Helpers
    private func onSignIn() {
        Task { @MainActor in
            do {
                isLoading = true
                try await sessionViewModel.signIn(email: email, password: password)
                isLoading = false
            } catch {
                isLoading = false
                errorMessage = error.localizedDescription
                showAlert = true
            }
        }
    }
}
