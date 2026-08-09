package com.attendance.service;

import com.attendance.model.AttendanceResponse;
import com.attendance.model.ExternalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private ExternalAttendanceService externalAttendanceService;

    DateTimeFormatter DATE_FORMAT =
            new java.time.format.DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-yyyy")
                    .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private static final Map<String, Integer> MONTH_MAP = new HashMap<>();

    static {
        MONTH_MAP.put("january", 1);
        MONTH_MAP.put("jan", 1);
        MONTH_MAP.put("february", 2);
        MONTH_MAP.put("feb", 2);
        MONTH_MAP.put("march", 3);
        MONTH_MAP.put("mar", 3);
        MONTH_MAP.put("april", 4);
        MONTH_MAP.put("apr", 4);
        MONTH_MAP.put("may", 5);
        MONTH_MAP.put("june", 6);
        MONTH_MAP.put("jun", 6);
        MONTH_MAP.put("july", 7);
        MONTH_MAP.put("jul", 7);
        MONTH_MAP.put("august", 8);
        MONTH_MAP.put("aug", 8);
        MONTH_MAP.put("september", 9);
        MONTH_MAP.put("sep", 9);
        MONTH_MAP.put("october", 10);
        MONTH_MAP.put("oct", 10);
        MONTH_MAP.put("november", 11);
        MONTH_MAP.put("nov", 11);
        MONTH_MAP.put("december", 12);
        MONTH_MAP.put("dec", 12);
    }

    public AttendanceResponse getAttendanceByEmpIdAndMonth(String empId, String month) {
        YearMonth yearMonth = parseMonth(month);
        
        if (yearMonth == null) {
            return new AttendanceResponse();
        }
        
        // Get first and last day of the month
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        String fromDate = firstDay.format(DATE_FORMAT);
        String toDate = lastDay.format(DATE_FORMAT);

        // Call external API
        ExternalApiResponse externalResponse = externalAttendanceService.getAttendanceData(empId, fromDate, toDate);

        return buildAttendanceResponse(empId, yearMonth, externalResponse);
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isEmpty()) {
            return null;
        }

        String lowerMonth = month.toLowerCase().trim();

        // Check if it's a month name (jan, feb, etc.)
        if (MONTH_MAP.containsKey(lowerMonth)) {
            int monthValue = MONTH_MAP.get(lowerMonth);
            int year = java.time.Year.now().getValue();
            return YearMonth.of(year, monthValue);
        }

        // Check if it's YYYY-MM format
        if (lowerMonth.matches("\\d{4}-\\d{2}")) {
            try {
                return YearMonth.parse(lowerMonth);
            } catch (Exception e) {
                return null;
            }
        }

        // Check if it's just month number (1-12)
        try {
            int monthValue = Integer.parseInt(lowerMonth);
            if (monthValue >= 1 && monthValue <= 12) {
                int year = java.time.Year.now().getValue();
                return YearMonth.of(year, monthValue);
            }
        } catch (NumberFormatException e) {
            // Not a number
        }

        return null;
    }

    private AttendanceResponse buildAttendanceResponse(String empId, YearMonth yearMonth, 
                                                       ExternalApiResponse externalResponse) {
        AttendanceResponse response = new AttendanceResponse();
        response.setEmpId(empId);
        response.setMonth(String.format("%02d", yearMonth.getMonthValue()));
        response.setYear(String.valueOf(yearMonth.getYear()));

        List<AttendanceResponse.AttendanceDetail> details = null;
        int totalDays = 0;
        int presentDays = 0;
        int morningShiftDays = 0;
        int eveningShiftDays = 0;
        int nightShiftDays = 0;
        double totalWorkingHours = 0.0;

        if (externalResponse != null && externalResponse.getExist() && 
            externalResponse.getData() != null && externalResponse.getData().getDt() != null) {

            List<String> punches = externalResponse.getData().getDt();
            
            // Group punches by date
            Map<LocalDate, List<LocalTime>> punchesByDate = groupPunchesByDate(punches);
            
            // Process each day to determine shift
            Map<LocalDate, AttendanceResponse.AttendanceDetail> attendanceMap = new TreeMap<>();
            
            for (Map.Entry<LocalDate, List<LocalTime>> entry : punchesByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<LocalTime> dailyPunches = entry.getValue();
                
                if (!dailyPunches.isEmpty()) {
                    LocalTime firstPunch = dailyPunches.get(0);
                    LocalTime lastPunch = dailyPunches.get(dailyPunches.size() - 1);
                    
                    ShiftInfo shiftInfo = detectShift(firstPunch, lastPunch);
                    AttendanceResponse.AttendanceDetail detail = new AttendanceResponse.AttendanceDetail(
                        date.format(DATE_FORMAT),
                        shiftInfo.shiftType,
                        shiftInfo.workingHours,
                        firstPunch.format(TIME_FORMAT) + " to " + lastPunch.format(TIME_FORMAT)
                    );
                    
                    attendanceMap.put(date, detail);
                }
            }

            processNightShifts(attendanceMap, punchesByDate);
            details = attendanceMap.values().stream().collect(Collectors.toList());
            totalDays = details.size();
            //Count shift types
            for (AttendanceResponse.AttendanceDetail detail : details) {
                String shiftType = detail.getShiftType();
                if ("GENERAL_SHIFT".equals(shiftType)) {
                    presentDays++;
                } else if ("MORNING_SHIFT".equals(shiftType)) {
                    morningShiftDays++;
                } else if ("EVENING_SHIFT".equals(shiftType)) {
                    eveningShiftDays++;
                } else if ("NIGHT_SHIFT".equals(shiftType)) {
                    nightShiftDays++;
                }
            }
            totalWorkingHours = details.stream().mapToDouble(d -> d.getWorkingHours() != null ? d.getWorkingHours() : 0.0).sum();
        } else {
            details = List.of();
        } response.setTotalWorkingDays(totalDays);
        response.setPresentDays(presentDays);
        response.setMorningShiftDays(morningShiftDays);
        response.setEveningShiftDays(eveningShiftDays);
        response.setNightShiftDays(nightShiftDays);
        response.setAbsentDays(0);
        response.setLeaveDays(0);
        response.setHalfDays(0);
        response.setTotalWorkingHours(totalWorkingHours);
        response.setAttendanceDetails(details);
        return response;
    }

    private Map<LocalDate, List<LocalTime>> groupPunchesByDate(List<String> punches) {

        Map<LocalDate, List<LocalTime>> grouped = new TreeMap<>();

        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("JAN", "01");
        monthMap.put("FEB", "02");
        monthMap.put("MAR", "03");
        monthMap.put("APR", "04");
        monthMap.put("MAY", "05");
        monthMap.put("JUN", "06");
        monthMap.put("JUL", "07");
        monthMap.put("AUG", "08");
        monthMap.put("SEP", "09");
        monthMap.put("OCT", "10");
        monthMap.put("NOV", "11");
        monthMap.put("DEC", "12");

        for (String punch : punches) {
            try {
                // Example:
                // 01-JUL-2026  :  07:03 AM

                String[] parts = punch.split("\\s+:\\s+", 2);

                if (parts.length != 2) {
                    continue;
                }

                String datePart = parts[0].trim();
                String timePart = parts[1].trim();

                // Convert 01-JUL-2026 -> 01-07-2026
                String[] dateParts = datePart.split("-");

                if (dateParts.length != 3) {
                    continue;
                }

                String day = dateParts[0];
                String month = dateParts[1].toUpperCase(Locale.ENGLISH);
                String year = dateParts[2];

                String monthNumber = monthMap.get(month);

                if (monthNumber == null) {
                    continue;
                }

                String numericDate = day + "-" + monthNumber + "-" + year;

                LocalDate date = LocalDate.parse(
                        numericDate,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                );

                // Handle both:
                // 07:03 AM
                // 17:31 PM
                String[] timeParts = timePart.split("\\s+");

                if (timeParts.length != 2) {
                    continue;
                }

                String hourMinute = timeParts[0];
                String amPm = timeParts[1].toUpperCase(Locale.ENGLISH);

                String[] hm = hourMinute.split(":");

                if (hm.length != 2) {
                    continue;
                }

                int hour = Integer.parseInt(hm[0]);
                int minute = Integer.parseInt(hm[1]);

                LocalTime time;

                if (hour > 12) {
                    // API sends values like 17:31 PM / 21:04 PM.
                    // Treat the numeric hour as 24-hour time.
                    time = LocalTime.of(hour, minute);
                } else {
                    // Normal values like 07:03 AM / 09:35 AM
                    if ("PM".equals(amPm) && hour < 12) {
                        hour += 12;
                    }

                    if ("AM".equals(amPm) && hour == 12) {
                        hour = 0;
                    }

                    time = LocalTime.of(hour, minute);
                }

                grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(time);

            } catch (Exception e) {
                System.out.println("Failed: " + punch);
                e.printStackTrace();
            }
        }

        // Sort punches by time for each day
        for (List<LocalTime> times : grouped.values()) {
            times.sort(LocalTime::compareTo);
        }

        return grouped;
    }

    private ShiftInfo detectShift(LocalTime firstPunch, LocalTime lastPunch) {
        // General Shift: 9am-10am to 5pm-6pm
        if (isInTimeRange(firstPunch, 7, 0, 11, 0) && isInTimeRange(lastPunch, 16, 0, 20, 0)) {
            double hours = calculateWorkingHours(firstPunch, lastPunch);
            return new ShiftInfo("GENERAL_SHIFT", hours);
        }
        
        // Morning Shift: 6:30am-7am to 12:30pm-1:30pm
        if (isInTimeRange(firstPunch, 5, 30, 7, 30) && isInTimeRange(lastPunch, 12, 00, 14, 30)) {
            double hours = calculateWorkingHours(firstPunch, lastPunch);
            return new ShiftInfo("MORNING_SHIFT", hours);
        }
        
        // Evening Shift: 12:30pm-1:30pm to 8:30pm-9:30pm
        if (isInTimeRange(firstPunch, 12, 00, 14, 00) && isInTimeRange(lastPunch, 20, 30, 22, 00)) {
            double hours = calculateWorkingHours(firstPunch, lastPunch);
            return new ShiftInfo("EVENING_SHIFT", hours);
        }
        
        // Night shift detection happens separately (previous day end to next day start)
        // Default: Present
        double hours = calculateWorkingHours(firstPunch, lastPunch);
        return new ShiftInfo("PRESENT", hours);
    }

    private void processNightShifts(Map<LocalDate, AttendanceResponse.AttendanceDetail> attendanceMap,
                                    Map<LocalDate, List<LocalTime>> punchesByDate) {

        // Dates whose morning punch was consumed as an OUT-punch by a
        // preceding night shift. We only remove these at the end, and only
        // if they didn't themselves become the START of another night shift.
        Set<LocalDate> consumedDates = new HashSet<>();

        // Sort chronologically so that when shift N ends on day N+1,
        // and shift N+1 also starts on day N+1, both get evaluated correctly
        // regardless of the original map's iteration order.
        List<LocalDate> sortedDates = new ArrayList<>(punchesByDate.keySet());
        Collections.sort(sortedDates);
        int count = 0;
        for (LocalDate date : sortedDates) {
            boolean finishedTwoNights = false;
            List<LocalTime> dailyPunches = punchesByDate.get(date);
            if (dailyPunches == null || dailyPunches.isEmpty()) {
                continue;
            }

            LocalTime firstPunch;
            if(count ==  0) {
                firstPunch = dailyPunches.get(0);
            } else {
                firstPunch = dailyPunches.get(dailyPunches.size() - 1);
                finishedTwoNights = true;
            }
            count = 1;
            /*
             * Night shift starts around:
             * 20:30 - 22:00
             */
            if (!isNightShiftStart(firstPunch)) {
                continue;
            }

            LocalDate nextDate = date.plusDays(1);
            List<LocalTime> nextDayPunches = punchesByDate.get(nextDate);
            if (nextDayPunches == null || nextDayPunches.isEmpty()) {
                continue;
            }

            /*
             * Next day's first punch should be the night-shift OUT punch.
             * Example:
             * 01-JUL 21:04
             * 02-JUL 07:03
             */
            LocalTime nextDayFirstPunch = nextDayPunches.get(0);
            if (!isNightShiftEnd(nextDayFirstPunch)) {
                continue;
            }

            double hours = calculateNightShiftHours(firstPunch, nextDayFirstPunch);
            AttendanceResponse.AttendanceDetail detail = new AttendanceResponse.AttendanceDetail(
                    date.format(DATE_FORMAT),
                    "NIGHT_SHIFT",
                    hours,
                    firstPunch.format(TIME_FORMAT) + " to " + nextDayFirstPunch.format(TIME_FORMAT));

            // Put the NIGHT_SHIFT against the date on which the shift started.
            attendanceMap.put(date, detail);

            // Mark next day's normal record for removal — but defer the actual
            // removal until we've finished scanning, in case nextDate turns out
            // to be the start of ANOTHER night shift (back-to-back case).

           if(finishedTwoNights) {
               attendanceMap.remove(nextDate);
           }

        }


    }

    private boolean isNightShiftStart(LocalTime time) {

        return isInTimeRange(
                time,
                20, 30,
                22, 0
        );
    }
    private boolean isNightShiftEnd(LocalTime time) {

        return isInTimeRange(
                time,
                6, 0,
                10, 0
        );
    }
    private double calculateNightShiftHours(
            LocalTime start,
            LocalTime end) {

        long startMinutes =
                start.getHour() * 60L
                        + start.getMinute();

        long endMinutes =
                end.getHour() * 60L
                        + end.getMinute();

        // Cross midnight
        long totalMinutes =
                (24 * 60 - startMinutes)
                        + endMinutes;

        return Math.round(
                totalMinutes / 60.0 * 100.0
        ) / 100.0;
    }

    private LocalTime extractFirstTime(String timeRange) {
        try {
            // Format: "HH:MM AM to HH:MM AM"
            String[] parts = timeRange.split(" to ");
            if (parts.length > 0) {
                return LocalTime.parse(parts[0].trim(), TIME_FORMAT);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private LocalTime extractLastTime(String timeRange) {
        try {
            // Format: "HH:MM AM to HH:MM AM"
            String[] parts = timeRange.split(" to ");
            if (parts.length > 1) {
                return LocalTime.parse(parts[1].trim(), TIME_FORMAT);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private boolean isInTimeRange(LocalTime time, int startHour, int startMin, int endHour, int endMin) {
        if (time == null) {
            return false;
        }
        LocalTime start = LocalTime.of(startHour, startMin);
        LocalTime end = LocalTime.of(endHour, endMin);
        return !time.isBefore(start) && !time.isAfter(end);
    }

    private double calculateWorkingHours(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return 0.0;
        }
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(start, end);
        return Math.round(minutes / 60.0 * 100.0) / 100.0;
    }

    private static class ShiftInfo {
        String shiftType;
        double workingHours;
        
        ShiftInfo(String shiftType, double workingHours) {
            this.shiftType = shiftType;
            this.workingHours = workingHours;
        }
    }
}

