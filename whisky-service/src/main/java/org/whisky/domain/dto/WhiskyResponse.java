package org.whisky.domain.dto;


import org.whisky.domain.entity.Whisky;
import org.whisky.domain.entity.WhiskyMetaData;

public record WhiskyResponse(String whiskyId, String name, String nose, String imageUrl, WhiskyMetaData metaData) {


    public static WhiskyResponse toWhiskyDto(Whisky whisky){
        return new WhiskyResponse(
                whisky.getId(),
                whisky.getName(),
                whisky.getNose(),
                whisky.getImageUrl(),
                whisky.getMetadata()
                );
    }
}
