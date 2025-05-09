package id.ac.ui.cs.gatherlove.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String profilePictureUrl;
    private String bio;
}
