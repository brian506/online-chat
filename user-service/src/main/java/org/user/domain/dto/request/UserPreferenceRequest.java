package org.user.domain.dto.request;

import org.user.domain.entity.Level;
import org.user.domain.entity.Taste;

public record UserPreferenceRequest(Level level, Taste taste) {
}
