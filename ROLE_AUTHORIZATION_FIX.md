# Role Authorization Fix: Allowing Admins to Access Customer Routes

## 1. The Problem: Why Admins Were Being Blocked

The error "Access denied. Customer privileges required" occurred because the security check was too strict. The `ensureCustomer` method in the `SecurityUtil` class was checking **only** for the "CUSTOMER" role:

```java
// Old, incorrect code
if (roles == null || !roles.contains("CUSTOMER")) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Customer privileges required.");
}
```

When a user with the "ADMIN" role accessed a customer-level endpoint, this check would fail, and a 403 Forbidden error would be returned.

## 2. The Solution: A More Flexible Role Check

The fix is to update the `ensureCustomer` method to check for **either** the "CUSTOMER" or "ADMIN" role. This ensures that admins are not blocked from accessing customer-level endpoints.

### a. The Corrected `SecurityUtil.java`

```java
package com.example.userservice.util;

import com.example.userservice.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class SecurityUtil {

    public static void ensureAdmin(String token) {
        List<String> roles = JwtUtil.extractRoles(token);
        if (roles == null || !roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admin privileges required.");
        }
    }

    public static void ensureCustomer(String token) {
        List<String> roles = JwtUtil.extractRoles(token);
        if (roles == null || (!roles.contains("CUSTOMER") && !roles.contains("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Customer or Admin privileges required.");
        }
    }
}
```

### b. Alternative Solution: `@PreAuthorize`

If you were using `@PreAuthorize` annotations, the same logic would apply. You would use `hasAnyRole` to check for multiple roles:

```java
// Incorrect:
@PreAuthorize("hasRole('CUSTOMER')")

// Correct:
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
```

## 3. Best Practice: Role Hierarchy

A cleaner and more scalable way to handle this in Spring Security is to establish a **role hierarchy**. This allows you to define that admins are a superset of customers, so they automatically inherit all of a customer's permissions.

To implement this, you would create a `RoleHierarchy` bean in your `SecurityConfig`:

```java
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy = "ROLE_ADMIN > ROLE_CUSTOMER";
    roleHierarchy.setHierarchy(hierarchy);
    return roleHierarchy;
}
```

With this in place, any time a security check is performed for `ROLE_CUSTOMER`, it will automatically pass for a user with `ROLE_ADMIN`. This simplifies your code, as you no longer need to check for both roles in your `ensureCustomer` method or `@PreAuthorize` annotations.
