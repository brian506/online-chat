package org.common.event;



public record CommentEvent(String commentId,String boardId, String receiverId,String writerId, String writerNickname, String comment) {

}
