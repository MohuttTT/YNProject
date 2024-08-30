package org.zerock.chain.pse.service;

import org.zerock.chain.pse.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByType(int empNo, String notificationType); // 알림 타입별 조회
    List<Notification> getAllNotifications(int empNo); // 사원 번호로 모든 알림 조회
    void deleteAllNotifications(int empNo);  // 사원 번호로 모든 알림 삭제
    void deleteNotification(Long notificationNo);   // 알림 번호로 개별 삭제

    // 전자결재 알림을 생성하는 메서드 추가 (영민)
    void createApprovalNotification(int docNo, String docTitle, String senderName, String docStatus, String withdraw);
}
