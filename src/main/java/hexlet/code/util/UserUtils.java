package hexlet.code.util;

import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Правило «менять и удалять можно только себя» — в одном месте, для @PreAuthorize. */
@Component("userUtils")
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    public boolean isCurrentUser(Long userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return userRepository
                .findById(userId)
                .map(user -> user.getEmail().equals(authentication.getName()))
                .orElse(false);
    }
}
