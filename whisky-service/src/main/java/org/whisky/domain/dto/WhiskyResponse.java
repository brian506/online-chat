package org.whisky.domain.dto;


import org.whisky.domain.entity.Whisky;

public record WhiskyResponse(String name, String description, String country, String type, String bottler, Integer age
         , Double abv) {

    public static WhiskyResponse toWhiskyDto(Whisky whisky){
        return new WhiskyResponse(
                whisky.getName(),
                whisky.getDescription(),
                whisky.getMetadata().getCountry(),
                whisky.getMetadata().getType(),
                whisky.getMetadata().getBottler(),
                whisky.getMetadata().getAge(),
                whisky.getMetadata().getAbv()
                );
    }
}
