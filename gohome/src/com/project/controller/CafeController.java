package com.project.controller;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.project.model.User;
import com.project.model.StudentUser;
import com.project.model.AdultUser;

public class CafeController {
    private ArrayList<User> userList;
    private String[] seats; 
    private LocalDateTime[] checkInTimes; 
    
    public CafeController() {
        userList = new ArrayList<>();
        seats = new String[10];
        checkInTimes = new LocalDateTime[10];
    }
    
    // 1-1번 메뉴: 신규 회원 등록 (학생/성인 분기)
    public String registerUser(int type, String id, String name, String phone, String birthDate, int hours) {
        try {
            User newUser = null;
            String rateInfo = "";
            
            if (type == 1) {
                newUser = new StudentUser(id, name, phone, birthDate);
                rateInfo = "\n[결제 금액 안내] 학생 10% 할인이 적용되었습니다.";
            } else {
                newUser = new AdultUser(id, name, phone, birthDate);
                rateInfo = "\n[결제 금액 안내] 성인 정가 요금이 적용되었습니다.";
            }

            int totalPrice = newUser.calculatePrice(hours);
            newUser.setTotalPaid(totalPrice);
            newUser.setRemainingHours(hours); 
            
            userList.add(newUser);
            return String.format("%s\n총 결제 금액: %d원\n[안내] 신규 회원 등록 및 이용권 결제가 완료되었습니다.", rateInfo, totalPrice);
            
        } catch (Exception e) {
            e.printStackTrace();
            return "[오류] 회원 등록 중 시스템 오류가 발생했습니다.";
        }
    }

    //1-2번 메뉴: 기존 회원 이용권 시간 충전
    public String updateUserHours(String id, int hours) {
        User foundUser = findUserById(id);
        if (foundUser == null) {
            return "[오류] 등록되지 않은 회원입니다. 신규 등록을 먼저 진행해주세요.";
        }

        try {
            int additionalPrice = foundUser.calculatePrice(hours);
            foundUser.setTotalPaid(foundUser.getTotalPaid() + additionalPrice);
            foundUser.setRemainingHours(foundUser.getRemainingHours() + hours);

            return String.format("\n[충전 완료]\n 추가 결제 금액: %d원\n 누적 잔여 시간: %d시간\n[안내] 이용권 충전이 완료되었습니다.", 
                    additionalPrice, foundUser.getRemainingHours());
        } catch (Exception e) {
            e.printStackTrace();
            return "[오류] 시간 충전 중 시스템 오류가 발생했습니다.";
        }
    }
    
    //2번 메뉴: 아이디 상세 조회
    public String getUserInfo(String id) {
        User foundUser = findUserById(id);
        
        if (foundUser == null) {
            return "[오류] 등록되지 않은 회원 정보입니다. 아이디를 다시 확인해주세요.";
        }
        
        int seatNo = -1;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] != null && seats[i].equalsIgnoreCase(id)) {
                seatNo = i + 1;
                break;
            }
        }
        
        String result = "\n [조회 결과]\n"
                      + "[조회] 이름: " + foundUser.getName() + "\n"
                      + "[조회] 잔여 시간: " + foundUser.getRemainingHours() + "시간\n";
                      
        if (seatNo != -1) {
            result += "[조회] 상태: 입실 중 (" + seatNo + "번 좌석 / 입실 시각: " + checkInTimes[seatNo - 1] + ")"; 
        } else {
            result += "[조회] 상태: 퇴실(미입실) 상태";
        }
        
        return result;
    }
    
    //3번 메뉴: 좌석 입실 배정
    public String checkIn(String id, int seatNo) {
        User foundUser = findUserById(id);
        
        if (foundUser == null) {
            return "[오류] 등록되지 않은 회원입니다. 먼저 등록 및 결제를 진행해주세요.";
        }
        
        if (foundUser.getRemainingHours() <= 0) {
            return "[오류] 잔여 시간이 없습니다. 충전 후 이용해주세요";
        }

        if (seatNo < 1 || seatNo > 10) {
            return "[오류] 좌석 번호는 1번부터 10번까지만 존재합니다.";
        }

        if (seats[seatNo - 1] != null) {
            return "[오류] 해당 좌석은 이미 다른 이용자가 사용 중입니다.";
        }

        int existingSeatIndex = -1;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] != null && seats[i].equalsIgnoreCase(id)) {
                existingSeatIndex = i;
                break;
            }
        }

        try {
            if (existingSeatIndex != -1) {
                seats[existingSeatIndex] = null;
                LocalDateTime oldTime = checkInTimes[existingSeatIndex];
                checkInTimes[existingSeatIndex] = null;
                
                seats[seatNo - 1] = foundUser.getId();
                checkInTimes[seatNo - 1] = oldTime; 
                
                return String.format("[변경] 이미 이용 중인 유저입니다. %d번 좌석에서 %d번 좌석으로 변경 배정되었습니다.", 
                        (existingSeatIndex + 1), seatNo);
            }

            seats[seatNo - 1] = foundUser.getId();
            checkInTimes[seatNo - 1] = LocalDateTime.now();
            
            return "[안내] " + id + "님의 " + seatNo + "번 좌석 입실 처리가 완료되었습니다";
        } catch (Exception e) {
            e.printStackTrace();
            return "[오류] 입실 처리 중 문제가 발생했습니다.";
        }
    }
    
    // 4번 메뉴: 좌석 퇴실 반납
    public String checkOut(String id) {
        User foundUser = findUserById(id);
        
        if (foundUser == null) {
            return "[오류] 등록된 고객 정보가 없습니다.";
        }
        
        int occupiedSeatIndex = -1;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] != null && seats[i].equalsIgnoreCase(id)) {
                occupiedSeatIndex = i;
                break;
            }
        }

        if (occupiedSeatIndex == -1) {
            return "[오류] 현재 입실 중인 좌석이 없습니다.";
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            long minutes = Duration.between(checkInTimes[occupiedSeatIndex], now).toMinutes();
            
            int hoursToDeduct = (int) (minutes / 60);
            if (hoursToDeduct == 0) {
                hoursToDeduct = 1; 
            }

            int remainingHours = Math.max(0, foundUser.getRemainingHours() - hoursToDeduct);
            foundUser.setRemainingHours(remainingHours);
            
            seats[occupiedSeatIndex] = null;
            checkInTimes[occupiedSeatIndex] = null;
            
            return String.format("[안내] 퇴실처리가 완료되었습니다. (%d번 좌석 반납 완료)\n"
                                + "[정산] 이용 시간: %d분 (실제 %d시간 차감)\n"
                                + "[정산] 남은 잔여 시간: %d시간", 
                                (occupiedSeatIndex + 1), minutes, hoursToDeduct, remainingHours);
        } catch (Exception e) {
            e.printStackTrace();
            return "[오류] 퇴실 정산 중 문제가 발생했습니다.";
        }
    }
    
    //데이터 저장
    public String saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (User u : userList) {
                String typeStr = (u instanceof StudentUser) ? "Student" : "Adult";

                bw.write(String.format("%s|%s|%s|%s|%s|%d|%d", 
                        typeStr, u.getId(), u.getName(), u.getPhone(), u.getBirthDate(), u.getRemainingHours(), u.getTotalPaid()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "[오류] 파일 저장 중 문제가 발생했습니다.";
        }
        return "[파일 저장 완료] 이용자 데이터(users.txt)가 안전하게 저장되었습니다.";
    }

    // 데이터 로드
    public void loadFromFile() {
        File file = new File("users.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String typeStr = data[0];
                String id = data[1];
                String name = data[2];
                String phone = data[3];
                String birthDate = data[4];
                int remHours = Integer.parseInt(data[5]);
                int totalPaid = Integer.parseInt(data[6]);
                
                User user = null;
                if (typeStr.equals("Student")) {
                    user = new StudentUser(id, name, phone, birthDate);
                } else {
                    user = new AdultUser(id, name, phone, birthDate);
                }
                
                user.setRemainingHours(remHours);
                user.setTotalPaid(totalPaid);
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserCount() { return userList.size(); }
    public String[] getSeats() { return seats; }

    public User findUserById(String id) {
        for (User u : userList) {
            if (u != null && u.getId().equalsIgnoreCase(id)) {
                return u;
            }           
        }
        return null;
    }
}