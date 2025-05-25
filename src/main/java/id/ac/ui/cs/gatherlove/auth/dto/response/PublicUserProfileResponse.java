package id.ac.ui.cs.gatherlove.auth.dto.response;

import id.ac.ui.cs.gatherlove.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserProfileResponse {
    private UUID id;
    private String name;
    private String profilePictureUrl;
    private String bio;
    private LocalDateTime memberSince;

    public static PublicUserProfileResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new PublicUserProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfilePictureUrl(),
                user.getBio(),
                user.getCreatedAt()
        );
    }
}