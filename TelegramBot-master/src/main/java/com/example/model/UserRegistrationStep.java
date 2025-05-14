package com.example.model;

// Các bước bot hỏi khi User xài /register
public enum UserRegistrationStep {
    ASK_NAME,
    ASK_PHONE,
    ASK_EMAIL,
    CONFIRM,
    EDIT_NAME,
    EDIT_PHONE,
    EDIT_EMAIL
}