//
//  ContentView.swift
//  PanAmexicans
//
//  Created by Karen Gonzalez Velazquez on 10/02/25.
//

import FirebaseAuth
import SwiftUI

typealias PMUser = FirebaseAuth.User

struct LoginView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var showAlert: Bool = false
    @State private var isSecured: Bool = true

    var body: some View {
        VStack {
            TextField("Email", text: $email, prompt: Text("Enter you email"))
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .padding(10)
                .overlay {
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(.gray, lineWidth: 1)
                }
                .padding(.horizontal)

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
                sessionManager.signIn(email: email, password: password)
            }

            createButton("Create account") {
                sessionManager.createAccount(email: email, password: password)
            }
        }
        .padding()
        .alert("", isPresented: $showAlert, actions: {
            Button { } label: { Text("Ok") }
        }, message: {
            Text(sessionManager.alertError ?? "")
        })
        .onChange(of: sessionManager.alertError) { _, newValue in
            guard let newValue, !newValue.isEmpty else { return }
            showAlert = true
        }
    }

    // MARK: - Helpers
    private func createButton(
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
            LinearGradient(colors: [.blue, .purple], startPoint: .topLeading, endPoint: .bottomTrailing)
        )
        .cornerRadius(16)
        .padding(.horizontal)
    }
}
