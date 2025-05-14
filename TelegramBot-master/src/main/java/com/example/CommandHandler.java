package com.example;

import com.example.model.*;
import com.example.service.AirtableService;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.OffsetDateTime;
import java.util.*;

public class CommandHandler {
    private final AirtableService airtableService;
    private final Map<Long, UserRegistrationState> userStates = new HashMap<>();
    private final Set<Long> adminIds = new HashSet<>();

    public CommandHandler() {
        this.airtableService = new AirtableService();
        adminIds.add(7451061215L);
    }

    public void handleMessage(Message message, MyBot bot) {
        Long userId = message.getFrom().getId();
        String text = message.getText().trim();
        Long chatId = message.getChatId();

        if (userStates.containsKey(userId)) {
            UserRegistrationState state = userStates.get(userId);
            if (state != null) {
                if (text.equals("/edit_name")) {
                    send(bot, chatId, "Vui lòng nhập tên mới:");
                    state.setStep(UserRegistrationStep.ASK_NAME);
                    return;
                } else if (text.equals("/edit_phone")) {
                    send(bot, chatId, "Vui lòng nhập số điện thoại mới:");
                    state.setStep(UserRegistrationStep.ASK_PHONE);
                    return;
                } else if (text.equals("/edit_email")) {
                    send(bot, chatId, "Vui lòng nhập email mới:");
                    state.setStep(UserRegistrationStep.ASK_EMAIL);
                    return;
                } else if (text.equalsIgnoreCase("OK") && state.getStep() == UserRegistrationStep.CONFIRM) {
                    send(bot, chatId, "Cảm ơn bạn đã xác nhận! Thông tin của bạn đã được ghi nhận.");
                    userStates.remove(userId);
                    return;
                }
                
                if (text.equals("/cancel_registration")) {
                    userStates.remove(userId);
                    send(bot, chatId, "Bạn đã hủy quá trình đăng ký.");
                } 
                else if (text.startsWith("/")) {
                    send(bot, chatId, "Bạn đang trong quá trình đăng ký. Hãy hoàn thành hoặc nhập /cancel_registration để hủy.");
                } else {
                    String txt = message.getText().trim();
                    UserRegistrationState states = userStates.get(userId);
        
                    if (states == null) {
                        states = new UserRegistrationState();
                        userStates.put(userId, states);
                    }

                    switch (states.getStep()) {
                        case ASK_NAME:
                            if (txt.isEmpty()) {
                                send(bot, chatId, "Tên không được để trống. Vui lòng nhập lại:");
                            } else {
                                states.getUser().setFullName(txt);
                                states.setStep(UserRegistrationStep.ASK_PHONE);
                                send(bot, chatId, "Số điện thoại của bạn?");
                            }
                            break;
                        case ASK_PHONE:
                            if (!isValidPhone(txt)) {
                                send(bot, chatId, "Số điện thoại không hợp lệ (phải bắt đầu bằng +84 hoặc 0 và có 10 chữ số). Vui lòng nhập lại:");
                            } else {
                                states.getUser().setPhoneNumber(txt);
                                states.setStep(UserRegistrationStep.ASK_EMAIL);
                                send(bot, chatId, "Email của bạn?");
                            }
                            break;
                        case ASK_EMAIL:
                            if (!isValidEmail(txt)) {
                                send(bot, chatId, "Email không hợp lệ. Vui lòng nhập lại:");
                            } else {
                                states.getUser().setEmail(txt);
                                states.setStep(UserRegistrationStep.CONFIRM);
                                send(bot, chatId, formatUserSummary(states.getUser(), states.getEventIds()) +
                                    "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
                            }
                            break;
                        case CONFIRM:
                            break;
                        default:
                            send(bot, chatId, "Bước không hợp lệ. Vui lòng bắt đầu lại.");
                            break;
                    }
                }
                return;
            }
        }

        // Admin commands
        if (adminIds.contains(userId)) {
            if (text.equals("/admin")) {
                send(bot, chatId, "Admin Granted");
            }
            if (text.startsWith("/admin_list_registrations")) {
                String registrations = airtableService.getAllRegistrations();
                send(bot, chatId, registrations);
                return;
            }
            if (text.startsWith("/admin_approve ")) {
                String[] parts = text.split(" ");
                if (parts.length == 3) {
                    airtableService.updateRegistrationStatus(parts[1], parts[2], "Accepted");
                    send(bot, chatId, "Đã duyệt đăng ký cho user " + parts[1] + " tại sự kiện " + parts[2]);
                }
                return;
            }
        }

        // User commands
        if (text.equals("/myevents")) {
            String registeredEvents = airtableService.getUserRegistrations(userId.toString());
            send(bot, chatId, registeredEvents);
            return;
        }

        if(text.equals("/create_event")) {
            String[] parts = text.replace("/create_event", "").trim().split("\\s+", 6);

            if(parts.length < 6 || parts[0].isEmpty() || parts[1].isEmpty() || parts[2].isEmpty() || parts[3].isEmpty() || parts[4].isEmpty()) {
                send(bot, chatId, "Vui lòng cung cấp đầy đủ thông tin sự kiện: /create_event <event_id> <event_name> <time> <description> <group_link> <capacity>. Ví dụ: /create_event 001 Chao_Tan_Sinh_Vien 10:00 2025-09-05 \"Mô tả sự kiện\" \"https://link\" 100");
                return;
            }

            String eventId = parts[0];
            String eventName = parts[1];
            String eventTime = parts[2];
            String eventDescription = parts[3];
            String eventGroupLink = parts[4];
            int eventCapacity = Integer.parseInt(parts[5]);

            if(airtableService.eventExists(eventId)) {
                send(bot, chatId, "Sự kiện với ID " +  eventId + " đã tồn tại. Vui lòng chọn ID khác.");
                return;
            }

            Event event = new Event();
            event.setId(eventId);
            event.setEventName(eventName);
            event.setTime(eventTime);
            event.setDescription(eventDescription);
            event.setGroupLink(eventGroupLink);
            event.setCapacity(eventCapacity);
            airtableService.saveEvent(event);
            send(bot, chatId, "Đã tạo sự kiện mới: " + eventName);
            return;
        }

        if (text.equals("/help")) {
            send(bot, chatId, "Các lệnh hỗ trợ:\n" +
                    "/register <event_id1> <event_id2> ... - Đăng ký sự kiện\n" +
                    "/cancel <event_id1> <event_id2> ... - Hủy đăng ký sự kiện\n" +
                    "/event - Danh sách sự kiện\n" +
                    "/my_events - Danh sách sự kiện đã đăng ký\n" +
                    "/edit_name - Chỉnh sửa tên\n" +
                    "/edit_phone - Chỉnh sửa số điện thoại\n" +
                    "/edit_email - Chỉnh sửa email");
            return;
        }

        if (text.equals("/delete_event")) {
            String[] parts = text.replace("/delete_event", "").trim().split("\\s+");
            String eventId = parts[0];
            if (parts.length == 0 || parts[0].isEmpty()) {
                send(bot, chatId, "Vui lòng cung cấp ID sự kiện để xóa. Ví dụ: /delete_event 1");
                return;
            }
            airtableService.deleteEvent(eventId);
            send(bot, chatId, "Đã xóa sự kiện với ID: " + eventId);
            return;
        }

        if (text.startsWith("/register")) {
            String[] eventIds = text.replace("/register", "").trim().split("\\s+");
            if (eventIds.length == 0 || eventIds[0].isEmpty()) {
                send(bot, chatId, "Vui lòng cung cấp ít nhất một ID sự kiện. Ví dụ: /register 1 2 3");
                return;
            }

            // Check event registration from User
            List<String> validEventIds = new ArrayList<>();
            List<String> invalidEventIds = new ArrayList<>();
            for (String eventId : eventIds) {
                if (airtableService.eventExists(eventId)) {
                    validEventIds.add(eventId);
                } else {
                    invalidEventIds.add(eventId);
                }
            }

            if (!invalidEventIds.isEmpty()) {
                send(bot, chatId, "Các ID sự kiện không hợp lệ: " + String.join(", ", invalidEventIds) +
                        ". Vui lòng chọn lại. Danh sách sự kiện: /event");
                return;
            }

            // Check for existing registrations
            List<String> alreadyRegistered = new ArrayList<>();
            List<String> newEventIds = new ArrayList<>();
            for (String eventId : validEventIds) {
                if (airtableService.isUserRegistered(userId.toString(), eventId)) {
                    alreadyRegistered.add(eventId);
                } else {
                    newEventIds.add(eventId);
                }
            }

            if (!alreadyRegistered.isEmpty()) {
                send(bot, chatId, "Bạn đã đăng ký các sự kiện: " + String.join(", ", alreadyRegistered) +
                        ". Vui lòng chọn các sự kiện khác.");
                if (newEventIds.isEmpty()) {
                    return;
                }
            }

            if (newEventIds.isEmpty()) {
                return;
            }

            // Kiểm tra xem người dùng đã tồn tại trong bảng User chưa
            User existingUser = airtableService.getUserByTelegramId(userId.toString());
            UserRegistrationState state = new UserRegistrationState();
            state.setEventIds(newEventIds);
            userStates.put(userId, state);

            if (existingUser != null) {
                // Người dùng đã tồn tại, hiển thị thông tin và yêu cầu xác nhận
                state.setUser(existingUser);
                state.setStep(UserRegistrationStep.CONFIRM);
                System.out.println("User exists, moving to CONFIRM for events: " + newEventIds);
                send(bot, chatId, formatUserSummary(existingUser, state.getEventIds()) +
                        "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
            } else {
                // Người dùng chưa tồn tại, bắt đầu quy trình đăng ký mới
                System.out.println("Starting registration for user: " + userId + ", events: " + newEventIds);
                send(bot, chatId, "Họ tên của bạn là gì?");
            }
            return;
        }

        if (text.startsWith("/cancel")) {
            String[] eventIds = text.replace("/cancel", "").trim().split("\\s+");
            if (eventIds.length == 0 || eventIds[0].isEmpty()) {
                send(bot, chatId, "Vui lòng cung cấp ít nhất một ID sự kiện để hủy. Ví dụ: /cancel 1 2 3");
                return;
            }

            List<String> canceledEvents = new ArrayList<>();
            List<String> notRegisteredEvents = new ArrayList<>();
            for (String eventId : eventIds) {
                if (airtableService.isUserRegistered(userId.toString(), eventId)) {
                    airtableService.cancelRegistration(userId.toString(), eventId);
                    canceledEvents.add(eventId);
                } else {
                    notRegisteredEvents.add(eventId);
                }
            }

            StringBuilder response = new StringBuilder();
            if (!canceledEvents.isEmpty()) {
                response.append("Đã hủy đăng ký các sự kiện: ").append(String.join(", ", canceledEvents)).append("\n");
            }
            if (!notRegisteredEvents.isEmpty()) {
                response.append("Bạn chưa đăng ký các sự kiện: ").append(String.join(", ", notRegisteredEvents));
            }
            send(bot, chatId, response.toString());
            return;
        }

        if (text.equals("/event")) {
            String eventList = airtableService.getAllEvents();
            send(bot, chatId, eventList);
            return;
        }

        if (text.startsWith("/edit_")) {
            handleEditCommand(text, userId, bot, chatId);
            return;
        }

        if (text.equals("/my_events")) {
            String registeredEvents = airtableService.getUserRegistrations(userId.toString());
            send(bot, chatId, registeredEvents);
            return;
        }

        // Handle registration steps
        if (userStates.containsKey(userId)) {
            UserRegistrationState state = userStates.get(userId);
            User user = state.getUser();

            switch (state.getStep()) {
                case ASK_NAME:
                    if (text.isEmpty()) {
                        send(bot, chatId, "Tên không được để trống. Vui lòng nhập lại:");
                        return;
                    }
                    user.setFullName(text);
                    state.setStep(UserRegistrationStep.ASK_PHONE);
                    send(bot, chatId, "Số điện thoại của bạn?");
                    break;
                case ASK_PHONE:
                    if (!isValidPhone(text)) {
                        send(bot, chatId, "Số điện thoại không hợp lệ (phải bắt đầu bằng +84 hoặc 0 và có 10 chữ số). Vui lòng nhập lại:");
                        return;
                    }
                    user.setPhoneNumber(text);
                    state.setStep(UserRegistrationStep.ASK_EMAIL);
                    send(bot, chatId, "Email của bạn?");
                    break;
                case ASK_EMAIL:
                    if (!isValidEmail(text)) {
                        send(bot, chatId, "Email không hợp lệ. Vui lòng nhập lại:");
                        return;
                    }
                    user.setEmail(text);
                    user.setTelegramId(userId);
                    user.setDateRegistered(OffsetDateTime.now());
                    state.setStep(UserRegistrationStep.CONFIRM);
                    send(bot, chatId, formatUserSummary(user, state.getEventIds()) +
                            "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
                    break;
                case CONFIRM:
                    System.out.println("In CONFIRM step for user: " + userId + ", received text: " + text + ", state events: " + state.getEventIds());
                    if (!text.equalsIgnoreCase("OK")) {
                        System.out.println("Non-OK message received in CONFIRM step: " + text);
                        send(bot, chatId, "Vui lòng gửi 'OK' để xác nhận hoặc dùng /edit_name, /edit_phone, /edit_email để sửa.");
                        return;
                    }

                    System.out.println("Processing OK for user: " + userId + ", events: " + state.getEventIds());
                    List<String> eventIds = state.getEventIds();
                    if (eventIds == null || eventIds.isEmpty()) {
                        System.out.println("Error: No event IDs in state for user: " + userId);
                        send(bot, chatId, "Lỗi: Không có sự kiện nào để đăng ký. Vui lòng thử lại với /register.");
                        userStates.remove(userId);
                        return;
                    }

                    // Lưu thông tin người dùng vào bảng User (nếu chưa tồn tại)
                    User userConfirm = state.getUser();
                    userConfirm.setTelegramId(userId);
                    userConfirm.setDateRegistered(OffsetDateTime.now());
                    boolean userSaved = airtableService.saveUser(userConfirm);
                    if (!userSaved) {
                        System.out.println("Failed to save user: " + userId);
                        send(bot, chatId, "Lỗi khi lưu thông tin người dùng. Vui lòng thử lại sau.");
                        userStates.remove(userId);
                        return;
                    }

                    // Lưu đăng ký sự kiện
                    boolean allRegistrationsSaved = true;
                    List<String> failedEvents = new ArrayList<>();
                    for (String eventId : eventIds) {
                        System.out.println("Creating registration for user: " + userId + ", event: " + eventId);
                        Registration reg = new Registration();
                        reg.setUserId(userId.toString());
                        reg.setEventId(eventId);
                        reg.setStatus("Pending");
                        reg.setRegisteredTime(OffsetDateTime.now());
                        System.out.println("Attempting to save registration for event: " + eventId);
                        try {
                            boolean saved = airtableService.saveRegistration(reg);
                            System.out.println("Save registration result for event " + eventId + ": " + saved);
                            if (!saved) {
                                allRegistrationsSaved = false;
                                failedEvents.add(eventId);
                                System.out.println("Failed to save registration for event: " + eventId);
                            }
                        } catch (Exception e) {
                            allRegistrationsSaved = false;
                            failedEvents.add(eventId);
                            System.out.println("Exception saving registration for event: " + eventId + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    if (allRegistrationsSaved) {
                        System.out.println("All registrations saved successfully for user: " + userId + ", events: " + eventIds);
                        send(bot, chatId, "Đã lưu thông tin đăng ký sự kiện: " + String.join(", ", eventIds) +
                                ". Cảm ơn bạn! Vui lòng chờ duyệt từ ban tổ chức.");
                        userStates.remove(userId);
                    } else {
                        String errorMsg = "Lỗi khi lưu thông tin. Không thể đăng ký các sự kiện: " + String.join(", ", failedEvents) +
                                        ". Vui lòng thử lại sau.";
                        System.out.println("Registration failed: " + errorMsg);
                        send(bot, chatId, errorMsg);
                    }
                    break;
                case EDIT_NAME:
                    if (text.isEmpty()) {
                        send(bot, chatId, "Tên không được để trống. Vui lòng nhập lại:");
                        return;
                    }
                    user.setFullName(text);
                    state.setStep(UserRegistrationStep.CONFIRM);
                    send(bot, chatId, formatUserSummary(user, state.getEventIds()) +
                            "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
                    break;
                case EDIT_PHONE:
                    if (!isValidPhone(text)) {
                        send(bot, chatId, "Số điện thoại không hợp lệ (phải bắt đầu bằng +84 hoặc 0 và có 10 chữ số). Vui lòng nhập lại:");
                        return;
                    }
                    user.setPhoneNumber(text);
                    state.setStep(UserRegistrationStep.CONFIRM);
                    send(bot, chatId, formatUserSummary(user, state.getEventIds()) +
                            "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
                    break;
                case EDIT_EMAIL:
                    if (!isValidEmail(text)) {
                        send(bot, chatId, "Email không hợp lệ. Vui lòng nhập lại:");
                        return;
                    }
                    user.setEmail(text);
                    state.setStep(UserRegistrationStep.CONFIRM);
                    send(bot, chatId, formatUserSummary(user, state.getEventIds()) +
                            "\n\nThông tin đã đúng chưa? Gửi 'OK' để xác nhận, hoặc dùng các lệnh:\n/edit_name\n/edit_phone\n/edit_email để sửa.");
                    break;
            }
        }
    }

    private void handleEditCommand(String text, Long userId, MyBot bot, Long chatId) {
        UserRegistrationState state = userStates.get(userId);
        if (state == null || state.getStep() != UserRegistrationStep.CONFIRM) {
            send(bot, chatId, "Không thể chỉnh sửa lúc này. Vui lòng hoàn thành đăng ký trước.");
            return;
        }

        if (text.equals("/edit_name")) {
            state.setStep(UserRegistrationStep.EDIT_NAME);
            send(bot, chatId, "Nhập họ tên mới:");
        } else if (text.equals("/edit_phone")) {
            state.setStep(UserRegistrationStep.EDIT_PHONE);
            send(bot, chatId, "Nhập số điện thoại mới:");
        } else if (text.equals("/edit_email")) {
            state.setStep(UserRegistrationStep.EDIT_EMAIL);
            send(bot, chatId, "Nhập email mới:");
        }
    }

    private void send(MyBot bot, Long chatId, String text) {
        try {
            bot.execute(new SendMessage(chatId.toString(), text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^(\\+84|0)[0-9]{9}$");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private String formatUserSummary(User user, List<String> eventIds) {
        StringBuilder summary = new StringBuilder();
        summary.append("Thông tin bạn vừa nhập:\n")
               .append("Họ tên: ").append(user.getFullName()).append("\n")
               .append("SĐT: ").append(user.getPhoneNumber()).append("\n")
               .append("Email: ").append(user.getEmail()).append("\n")
               .append("Đã đăng kí Event: \n");

        if (eventIds != null && !eventIds.isEmpty()) {
            for (int i = 0; i < eventIds.size(); i++) {
                summary.append(i + 1).append(". ").append(eventIds.get(i)).append("\n");
            }
        } else {
            summary.append("Không có sự kiện nào được chọn.\n");
        }

        return summary.toString();
    }
}