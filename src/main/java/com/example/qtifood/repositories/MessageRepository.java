package com.example.qtifood.repositories;

import com.example.qtifood.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Lấy messages của conversation với pagination
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.sender " +
           "WHERE m.conversation.id = :conversationId " +
           "ORDER BY m.createdAt ASC")
    Page<Message> findByConversationIdWithSender(@Param("conversationId") Long conversationId, 
                                                 Pageable pageable);
    
    // Lấy messages của conversation (không phân trang)
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.sender " +
           "WHERE m.conversation.id = :conversationId " +
           "ORDER BY m.createdAt ASC")
    List<Message> findByConversationIdWithSender(@Param("conversationId") Long conversationId);
    
    // Lấy tin nhắn cuối cùng của conversation
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.sender " +
           "WHERE m.conversation.id = :conversationId " +
           "ORDER BY m.createdAt DESC")
    List<Message> findLastMessageByConversationId(@Param("conversationId") Long conversationId, 
                                                  Pageable pageable);
    
    // Đếm số tin nhắn trong conversation
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId")
    Long countByConversationId(@Param("conversationId") Long conversationId);
    
    // Đếm tin nhắn chưa đọc (giả sử có thêm field isRead sau này)
    @Query("SELECT COUNT(m) FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id != :userId")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, 
                            @Param("userId") String userId);
    
    // Lấy messages của user trong conversation
    @Query("SELECT m FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id = :senderId " +
           "ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdAndSenderId(@Param("conversationId") Long conversationId,
                                                 @Param("senderId") String senderId);
}