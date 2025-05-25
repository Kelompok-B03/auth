package id.ac.ui.cs.gatherlove.auth.dto.request; // Saya menyarankan sub-package 'request'

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @Nullable
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Nullable
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Nullable
    @Size(max = 255, message = "Bio cannot exceed 255 characters")
    private String bio;

    @Nullable
    private String profilePictureUrl;
}