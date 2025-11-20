package org.board.domain.dto.request;

import org.board.domain.entity.Tags;

import java.util.List;

public record CreateBoardRequest(String whiskyId, String title, String content, Double rating, List<Tags> tags) {
}
