package com.rulesengine.model.entities;

/**
 * Defines the fixed set of user roles available in the application.
 * Using an enum ensures type safety and prevents invalid role assignments.
 */
public enum Role {
    ROLE_VIEWER,
    ROLE_EDITOR,
    ROLE_ADMIN,
    ROLE_SCHEMA_APPROVER
}

