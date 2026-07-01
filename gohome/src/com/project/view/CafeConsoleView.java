package com.project.view;

import java.util.Scanner;
import com.project.controller.CafeController;

public class CafeConsoleView {
    private Scanner scanner = new Scanner(System.in);
    private CafeController controller = new CafeController(); 

    public void startApp() {
        controller.loadFromFile();
        
        while (true) {
            int choice = showMainMenu(controller.getUserCount(), controller.getSeats());
            
            switch (choice) {
                case 1:
                    System.out.println("\n---  이용권 메뉴 ---");
                    System.out.println("1. 신규 회원 등록 및 결제");
                    System.out.println("2. 기존 회원 이용권 충전");
                    System.out.print(" 원하시는 작업 번호를 선택하세요: ");
                    
                    while (!scanner.hasNextInt()) {
                        System.out.println(" [오류] 숫자만 입력 가능합니다.");
                        System.out.print(" 원하시는 작업 번호를 선택하세요: ");
                        scanner.next();
                    }
                    int subChoice = scanner.nextInt();
                    
                    if (subChoice == 1) {
                        int type = inputUserType();
                        if (type < 1 || type > 2) {
                            System.out.println(" [오류] 잘못된 선택입니다. 처음부터 다시 시도해주세요.");
                            break;
                        }
                        String[] info = inputUserInfo(); 
                        int hours = inputHours();
                        
                        String regResult = controller.registerUser(type, info[0], info[1], info[2], info[3], hours);
                        System.out.println(regResult);
                    } else if (subChoice == 2) {
                        scanner.nextLine();
                        System.out.print("시간을 충전할 회원 아이디를 입력하세요: ");
                        String chargeId = scanner.nextLine();
                        
                        if (controller.findUserById(chargeId) == null) {
                            System.out.println(" [오류] 등록되지 않은 아이디입니다.");
                            break;
                        }
                        int hours = inputHours();
                        String chargeResult = controller.updateUserHours(chargeId, hours);
                        System.out.println(chargeResult);
                    } else {
                        System.out.println(" [오류] 잘못된 번호 선택입니다.");
                    }
                    break;
                    
                case 2:
                    String searchId = inputSearchId();
                    String infoResult = controller.getUserInfo(searchId);
                    System.out.println(infoResult);
                    break;
                    
                case 3:
                    String inId = inputUserId();
                    int seatNo = inputSeatNo();
                    String inResult = controller.checkIn(inId, seatNo);
                    System.out.println(inResult);
                    break;
                    
                case 4:
                    String outId = inputCheckOutId();
                    String outResult = controller.checkOut(outId);
                    System.out.println(outResult);
                    break;
                    
                case 5:
                    String saveResult = controller.saveToFile();
                    System.out.println(saveResult);
                    System.out.println(" 프로그램을 안전하게 종료합니다. 이용해 주셔서 감사합니다.");
                    System.exit(0);
                    
                default:
                    System.out.println(" [오류] 잘못된 입력입니다. 1~5 사이의 숫자를 입력해주세요.");
            }
        }
    }

    private int showMainMenu(int currentUserCount, String[] seats) {
        System.out.println("\n==================================================");
        System.out.println("         STUDY CAFE MANAGEMENT SYSTEM         ");
        System.out.println("==================================================");
              
        System.out.println("=====  실시간 좌석 현황 =====");
        
        for (int i = 0; i < seats.length; i++) {
            String status = (seats[i] == null) ? "빈자리" : seats[i];
            System.out.printf("[%d번: %s]  ", (i + 1), status);
            if ((i + 1) % 5 == 0) System.out.println();
        }
        System.out.println("==================================================");
        
        System.out.println("1. 이용권 결제 및 등록/충전");
        System.out.println("2. 잔여 시간 상세 조회");
        System.out.println("3. 좌석 입실 배정");
        System.out.println("4. 좌석 퇴실 반납 ");
        System.out.println("5. 프로그램 종료");
        System.out.print("\n 메뉴 번호를 입력하세요: ");
        
        while (!scanner.hasNextInt()) {
            System.out.println(" [오류] 숫자가 아닙니다. 1~5 사이의 숫자를 입력해주세요.");
            System.out.print(" 메뉴 번호를 입력하세요: ");
            scanner.next();
        }
        
        return scanner.nextInt();
    }

    public int inputUserType() {
        System.out.print("이용자 유형을 선택하세요 (1.학생 2.성인): ");
        while (!scanner.hasNextInt()) {
            System.out.println(" [오류] 숫자만 입력 가능합니다.");
            System.out.print("이용자 유형을 선택하세요 (1.학생 2.성인): ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public String[] inputUserInfo() {
        scanner.nextLine();
        String[] info = new String[4];
        
        while (true) {
            System.out.print("아이디 입력: ");
            String id = scanner.nextLine();
            if (!id.matches("^[a-zA-Z0-9]{1,9}$")) {
                System.out.println(" [오류] 아이디는 영문 또는 숫자 조합의 9글자 이내여야 합니다.");
                continue;
            }
            if (controller.findUserById(id) != null) {
                System.out.println(" [오류] 이미 등록된 아이디입니다. 다른 아이디를 입력하거나 충전 메뉴를 이용해주세요.");
                continue;
            }
            info[0] = id;
            break;
        }
        
        while (true) {
            System.out.print("이름 입력: ");
            String name = scanner.nextLine();
            if (!name.matches("^[가-힣]{2,5}$")) {
                System.out.println(" [오류] 이름은 올바른 한글 2~5글자 사이여야 합니다.");
                continue;
            }
            info[1] = name;
            break;
        }
        
        while (true) {
            System.out.print("연락처 입력(예: 010-XXXX-XXXX): ");
            String phone = scanner.nextLine();
            if (!phone.matches("^010-\\d{3,4}-\\d{4}$")) {
                System.out.println(" [오류] 올바른 연락처 형식(010-XXXX-XXXX)이 아닙니다.");
                continue;
            }
            info[2] = phone;
            break;
        }
        
        while (true) {
            System.out.print("생년월일 6자리 입력(예: 080514): ");
            String birth = scanner.nextLine();
            if (!birth.matches("^[0-9]{6}$")) {
                System.out.println(" [오류] 생년월일은 정확히 숫자 6자리로 입력해야 합니다.");
                continue;
            }
            info[3] = birth;
            break;
        }
        
        return info;
    }

    public int inputHours() {
        System.out.print("이용하실 시간(시간 단위 숫자만): ");
        while (!scanner.hasNextInt()) {
            System.out.println(" [오류] 숫자 단위로만 입력이 가능합니다.");
            System.out.print("이용하실 시간(시간 단위 숫자만): ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public String inputSearchId() {
        System.out.println("\n---  잔여 시간 상세 조회 ---");
        System.out.print("조회할 회원 아이디를 입력하세요: ");
        scanner.nextLine(); 
        return scanner.nextLine();
    }

    public String inputUserId() {
        System.out.println("\n---  좌석 입실 배정 ---");
        System.out.print("회원 아이디를 입력하세요: ");
        scanner.nextLine(); 
        return scanner.nextLine();
    }

    public int inputSeatNo() {
        System.out.print("원하는 좌석 번호(1~10)를 입력하세요: ");
        while (!scanner.hasNextInt()) {
            System.out.println(" [오류] 올바른 좌석 번호(숫자)를 입력하세요.");
            System.out.print("원하는 좌석 번호(1~10)를 입력하세요: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public String inputCheckOutId() {
        System.out.println("\n---  좌석 퇴실 반납 ---");
        System.out.print("퇴실할 회원의 아이디를 입력하세요: ");
        scanner.nextLine(); 
        return scanner.nextLine();
    }
}