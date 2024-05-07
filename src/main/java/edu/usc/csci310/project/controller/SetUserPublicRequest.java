package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetUserPublicRequest {
    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("user_id")
    private Long userId;

    public SetUserPublicRequest() {}

    public SetUserPublicRequest(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Long getUserId() { return userId; };

    public void setUserId(Long userId) { this.userId = userId; };
}