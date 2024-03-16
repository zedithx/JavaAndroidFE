package com.example.javaandroidapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ErrorHandles {
    public static String signUpErrors(Task<AuthResult> error) {
        try {
            throw error.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            return "You have a weak password, your password should be 6 character long";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            return "Your credentials are badly formatted, it could be an invalid email";
        } catch (FirebaseAuthUserCollisionException e) {
            return "Your email has already been registered.";
        } catch (Exception e) {
            return e.toString();
        }
    }
}
