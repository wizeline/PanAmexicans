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
    @State private var firstName: String = ""
    @State private var lastName: String = ""
    @State private var isSecured: Bool = true
    @State private var isCreatingAccount: Bool = false
    @State private var showAlert: Bool = false

    var body: some View {
        NavigationStack {
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

                ViewHelper.createButton("Sign In") {
                    Task {
                        await sessionManager.signIn(email: email, password: password)
                    }
                }

                NavigationLink {
                    CreateAccountView()
                        .environmentObject(sessionManager)
                } label: {
                    Text("Create account")
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
    }
}

enum ViewHelper {
    static func createButton(
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

struct CreateAccountView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @State private var name: String = ""
    @State private var lastName: String = ""
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isSecured: Bool = true
    @State private var showAlert: Bool = false

    var body: some View {
        VStack {
            TextField("Name", text: $name, prompt: Text("Enter your name"))
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .padding(10)
                .overlay {
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(.gray, lineWidth: 1)
                }
                .padding(.horizontal)

            TextField("Last Name", text: $lastName, prompt: Text("Enter your last name"))
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .padding(10)
                .overlay {
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(.gray, lineWidth: 1)
                }
                .padding(.horizontal)

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

            ViewHelper.createButton("Create account") {
                createAccount()
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

    private func createAccount() {
        Task {
            await sessionManager.createAccount(
                name: name,
                lastName: lastName,
                email: email,
                password: password
            )
        }
    }
}
