package com.tara.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaraPrincipal {
    private String firebaseUid;
    private String email;
    private String name;
}
