package org.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Taste {

    SWEET("달콤"),
    FRUITY("과일"),
    CEREAL("곡물 향"),
    NUTTY("견과류"),
    FLORAL("꽃 향"),
    HERBAL("허브"),
    SPICE("향신료"),
    WOODY("나무/오크"),
    ROASTED("구운/로스티드"),
    DAIRY("유제품"),
    PEATY("피트 향"),
    SMOKY("스모키"),
    EARTHY("흙/미네랄"),
    CONFECTIONERY("디저트/캔디"),
    WINE_FINISH("와인/셰리 피니시"),
    MARITIME("바다/해양");

    private final String description;
}
