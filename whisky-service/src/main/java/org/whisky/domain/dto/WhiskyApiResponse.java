package org.whisky.domain.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.whisky.domain.entity.Whisky;
import org.whisky.domain.entity.WhiskyMetaData;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WhiskyApiResponse {

    private String id;
    private String name;
    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    private WhiskyMetaData metadata;

    public static Whisky toWhisky(WhiskyApiResponse response){
        return Whisky.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .imageUrl(response.imageUrl)
                .metadata(response.getMetadata())
                .build();
    }
}
