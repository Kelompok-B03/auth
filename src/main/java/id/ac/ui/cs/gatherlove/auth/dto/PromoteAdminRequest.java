package id.ac.ui.cs.gatherlove.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoteAdminRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
}